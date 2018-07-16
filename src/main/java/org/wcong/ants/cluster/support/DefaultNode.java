package org.wcong.ants.cluster.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.LifeCircle;
import org.wcong.ants.cluster.Cluster;
import org.wcong.ants.cluster.Distributer;
import org.wcong.ants.cluster.Node;
import org.wcong.ants.cluster.NodeConfig;
import org.wcong.ants.crawler.Parser;
import org.wcong.ants.crawler.Result;
import org.wcong.ants.document.DocumentReader;
import org.wcong.ants.document.DocumentWriter;
import org.wcong.ants.document.lucene.LuceneDocumentReader;
import org.wcong.ants.document.lucene.LuceneDocumentWriter;
import org.wcong.ants.downloader.Downloader;
import org.wcong.ants.downloader.support.DefaultDownloader;
import org.wcong.ants.http.HttpServerHandler;
import org.wcong.ants.http.handler.*;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.Spider;
import org.wcong.ants.spider.SpiderManager;
import org.wcong.ants.spider.support.DefaultSpiderManager;
import org.wcong.ants.transport.TransportMessage;
import org.wcong.ants.util.JsonUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.http.server.HttpServer;
import reactor.netty.tcp.TcpClient;
import reactor.netty.tcp.TcpServer;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * a node
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/28
 */
public class DefaultNode implements Node {

    private static Logger logger = LoggerFactory.getLogger(DefaultNode.class);

    private NodeConfig nodeConfig;

    private HttpServer httpServer = HttpServer.create();

    private DisposableServer disposableHttpServer;

    private TcpServer tcpServer = TcpServer.create();

    private DisposableServer disposableTcpServer;

    private TcpClient tcpClientToMaster;

    private Connection connectionToMaster;

    private Map<String, NettyOutbound> outboundMap = new ConcurrentHashMap<>();

    private SpiderManager spiderManager = new DefaultSpiderManager();

    private Downloader downloader = new DefaultDownloader();

    private Distributer distributer = new DefaultDistributer();

    private final DocumentWriter documentWriter = new LuceneDocumentWriter();

    private List<LifeCircle> lifeCircleList = new LinkedList<>();

    private Cluster cluster = new DefaultCluster();

    public void setNodeConfig(NodeConfig nodeConfig) {
        this.nodeConfig = nodeConfig;
    }

    public SpiderManager getSpiderManager() {
        return spiderManager;
    }

    public String getName() {
        return nodeConfig.getNodeName();
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void init() {
        documentWriter.setRootPath(nodeConfig.getDataPath());
        lifeCircleList.add(documentWriter);

        DocumentReader documentReader = new LuceneDocumentReader();
        documentReader.setRootPath(nodeConfig.getDataPath());
        ((LuceneDocumentReader) documentReader).setLuceneDocumentWriter((LuceneDocumentWriter) documentWriter);
        lifeCircleList.add(documentReader);

        cluster.setLocalNode(nodeConfig);

        spiderManager.loadSpider(nodeConfig.getSpiderPackages());

        distributer.setCluster(cluster);

        if (!nodeConfig.isLocalMaster()) {
            tcpClientToMaster = TcpClient.create()
                    .host(nodeConfig.getMasterIp())
                    .port(nodeConfig.getMasterPort())
                    .doOnConnected(con ->
                            con.outbound().send(Mono
                                    .just(TransportMessage.newRequestMessage(cluster.getLocalNode().getNodeName(), cluster.getLocalNode()))
                                    .map(JsonUtils.jsonEncoder))
                    )
                    .handle((in, out) -> in
                            .receive()
                            .asString()
                            .map(JsonUtils.jsonDecoder)
                            .map(message -> {
                                switch (message.getType()) {
                                    case TransportMessage.TYPE_REQUEST:
                                        Flux.fromStream(message.getRequestList().stream())
                                                .flatMap(this::downloader);
                                        break;
                                    case TransportMessage.TYPE_CONFIG:
                                        cluster.addNodeConfig(message.getNodeConfig());
                                        break;
                                }
                                return "";
                            }).then()

                    );

        }
        List<HttpServerHandler> serverHandlerList = Arrays.asList(
                new HttpServerDefaultHandler(),
                new HttpServerClusterHandler(),
                new HttpServerDocumentAnalysisHandler(documentReader),
                new HttpServerDocumentHandler(documentReader),
                new HttpServerDocumentsHandler(documentReader),
                new HttpServerSpiderHandler(this),
                new HttpServerSpidersHandler(this)
        );
        httpServer = httpServer
                .host("127.0.0.1")
                .port(nodeConfig.getHttpPort())
                .handle((req, res) -> {
                    for (HttpServerHandler handler : serverHandlerList) {
                        if (handler.test(req)) {
                            return handler.handleRequest(req, res);
                        }
                    }
                    return serverHandlerList.get(0).handleRequest(req, res);
                });

        tcpServer = tcpServer
                .port(nodeConfig.getTcpPort())
                .handle((in, out) ->
                        in
                                .receive()
                                .asString()
                                .map(JsonUtils.jsonDecoder)
                                .flatMap(message -> tcpServerHandler(message, out)).then()
                );

    }

    public Flux<Void> distributer(TransportMessage message) {
        return Flux
                .fromStream(message.getRequestList().stream())
                .buffer(10)
                .flatMap(request -> {
                    NodeConfig nodeConfig = distributer.selectConfig();
                    if (nodeConfig.isLocalMaster()) {
                        return Flux.fromStream(request.stream())
                                .flatMap(this::downloader);
                    } else {
                        return outboundMap
                                .get(nodeConfig.getNodeName())
                                .send(Mono.just(TransportMessage.newRequestMessage(cluster.getLocalNode().getNodeName(), request)).map(JsonUtils.jsonEncoder));
                    }
                });
    }

    private Mono<Void> tcpServerHandler(TransportMessage message, NettyOutbound out) {
        if (message.getType() == TransportMessage.TYPE_CONFIG) {
            cluster.addNodeConfig(message.getNodeConfig());
            outboundMap.put(message.getNodeConfig().getNodeName(), out);
            return Mono.empty();
        } else if (message.getType() == TransportMessage.TYPE_RESULT) {
            return distributer(message).then();
        }
        return Mono.empty();
    }

    private Mono<Void> downloader(Request req) {
        HttpClient httpClient = HttpClient.create()
                .baseUrl(req.getUrl());
        if (req.getHeader() != null) {
            httpClient = httpClient.headers(header -> req
                    .getHeader()
                    .forEach(header::set));
        }
        if (req.getCookies() != null) {
            httpClient = httpClient.headers(
                    header -> header.set("cookie", req.getCookies()
                            .entrySet().stream().map(e -> e.getKey() + ":" + e.getValue())
                            .reduce((v1, v2) -> v1 + ";" + v2)));
        }


        switch (req.getMethod()) {
            case "POST":
                return httpClient.post().send(ByteBufFlux.fromString(Mono.just(req.getBody())))
                        .response((res, content) -> parser(req, res, content)).then();
            case "PUT":
                return httpClient.put().send(ByteBufFlux.fromString(Mono.just(req.getBody())))
                        .response((res, content) -> parser(req, res, content)).then();
            case "DELETE":
                return httpClient.delete()
                        .send(ByteBufFlux.fromString(Mono.just(req.getBody())))
                        .response((res, content) -> parser(req, res, content)).then();
            default:
                return httpClient.get().response((res, content) -> parser(req, res, content)).then();
        }
    }

    private Mono<Void> parser(Request req, HttpClientResponse res, ByteBufFlux content) {
        content.asString().reduce((s1, s2) -> s1 + s2).flatMap(result -> {
            Spider spider = spiderManager.getSpider(req.getSpiderName());
            if (spider == null) {
                return Mono.empty();
            }
            Parser parser = spider.getParser(req.getParseName());
            if (parser == null) {
                return Mono.empty();
            }
            Result parseResult = parser.parse(downloader.makeResponse(req, res, result));
            if (parseResult.getDataList() != null) {
                for (Result.Data data : parseResult.getDataList()) {
                    try {
                        documentWriter.writeDocument(req.getSpiderName(), data.getIndex(), data.getData());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (parseResult.getRequestList() != null) {
                TransportMessage message = TransportMessage.newRequestMessage(req.getNodeName(), parseResult.getRequestList());
                if (nodeConfig.isLocalMaster()) {
                    distributer(message);
                } else {
                    return outboundMap.get(cluster.getMasterNode().getNodeName()).send(Mono.just(message).map(JsonUtils.jsonEncoder)).then();
                }
            }
            return Mono.empty();
        });
        return Mono.empty();
    }

    public void start() {

        disposableHttpServer = httpServer.bind().block();
        if (disposableHttpServer != null) {
            logger.info("start listen http at {}:{}", disposableHttpServer.host(), disposableHttpServer.port());
        }

        disposableTcpServer = tcpServer.bind().block();
        if (disposableTcpServer != null) {
            logger.info("start tcp http at {}:{}", disposableTcpServer.host(), disposableTcpServer.port());
        }

        if (tcpClientToMaster != null) {
            connectionToMaster = tcpClientToMaster.connectNow();
        }
    }

    public void pause() {

    }

    public void resume() {

    }

    public void stop() {
        disposableHttpServer.dispose();
        disposableTcpServer.dispose();
        if (connectionToMaster != null) {
            connectionToMaster.dispose();
        }
    }

    public void destroy() {
        for (LifeCircle lifeCircle : lifeCircleList) {
            lifeCircle.destroy();
        }
    }
}

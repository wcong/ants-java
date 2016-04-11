package org.wcong.ants.cluster.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.LifeCircle;
import org.wcong.ants.cluster.Cluster;
import org.wcong.ants.cluster.ClusterRequestBlockingQueue;
import org.wcong.ants.cluster.Distributer;
import org.wcong.ants.cluster.Node;
import org.wcong.ants.cluster.NodeConfig;
import org.wcong.ants.crawler.Crawler;
import org.wcong.ants.crawler.support.DefaultCrawler;
import org.wcong.ants.downloader.Downloader;
import org.wcong.ants.downloader.support.DefaultDownloader;
import org.wcong.ants.http.HttpServer;
import org.wcong.ants.index.DocumentReader;
import org.wcong.ants.index.DocumentWriter;
import org.wcong.ants.index.lucene.LuceneDocumentReader;
import org.wcong.ants.index.lucene.LuceneDocumentWriter;
import org.wcong.ants.spider.RequestBlockingQueue;
import org.wcong.ants.spider.ResponseBlockingQueue;
import org.wcong.ants.spider.SpiderManager;
import org.wcong.ants.spider.support.DefaultSpiderManager;
import org.wcong.ants.spider.support.LinkedRequestBlockingQueue;
import org.wcong.ants.spider.support.LinkedResponseBlockingQueue;
import org.wcong.ants.transport.ClientHandler;
import org.wcong.ants.transport.ServerHandler;
import org.wcong.ants.transport.TransportClient;
import org.wcong.ants.transport.TransportServer;

import java.util.LinkedList;
import java.util.List;

/**
 * a node
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/28
 */
public class DefaultNode implements Node {

	private static Logger logger = LoggerFactory.getLogger(DefaultNode.class);

	private NodeConfig nodeConfig;

	private HttpServer httpServer = new HttpServer();

	private SpiderManager spiderManager = new DefaultSpiderManager();

	private Crawler crawler = new DefaultCrawler();

	private Downloader downloader = new DefaultDownloader();

	private TransportClient transportClient;

	private TransportServer transportServer = new TransportServer();

	private Distributer distributer = new DefaultDistributer();

	private List<LifeCircle> lifeCircleList = new LinkedList<LifeCircle>();

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

		DocumentWriter documentWriter = new LuceneDocumentWriter();
		documentWriter.setRootPath(nodeConfig.getDataPath());
		lifeCircleList.add(documentWriter);

		DocumentReader documentReader = new LuceneDocumentReader();
		documentReader.setRootPath(nodeConfig.getDataPath());
		((LuceneDocumentReader) documentReader).setLuceneDocumentWriter((LuceneDocumentWriter) documentWriter);
		lifeCircleList.add(documentReader);

		ClusterRequestBlockingQueue clusterRequestQueue = new ClusterLinkedBlockingDeque();
		RequestBlockingQueue localRequestQueue = new LinkedRequestBlockingQueue();
		ResponseBlockingQueue localResponseQueue = new LinkedResponseBlockingQueue();

		clusterRequestQueue.setCluster(cluster);
		clusterRequestQueue.setQueue(localRequestQueue, localResponseQueue);

		cluster.setClusterQueue(clusterRequestQueue);
		cluster.setLocalNode(nodeConfig);

		spiderManager.loadSpider(nodeConfig.getSpiderPackages());
		spiderManager.setClusterQueue(clusterRequestQueue);

		crawler.setQueue(localRequestQueue, localResponseQueue);
		crawler.setSpiderManager(spiderManager);
		crawler.setClusterQueue(clusterRequestQueue);
		crawler.setDocumentWriter(documentWriter);

		downloader.setQueue(localRequestQueue, localResponseQueue);

		httpServer.setNode(this);
		httpServer.setPort(nodeConfig.getHttpPort());

		distributer.setCluster(cluster);
		distributer.setClusterQueue(clusterRequestQueue);

		ServerHandler.ServerInHandler inHandler = new ServerHandler.ServerInHandler();
		inHandler.setCluster(cluster);
		inHandler.setClusterQueue(clusterRequestQueue);
		transportServer.setServerInHandler(inHandler);
		transportServer.setPort(nodeConfig.getTcpPort());

		if (!nodeConfig.isLocalMaster()) {
			transportClient = new TransportClient(nodeConfig.getMasterIp(), nodeConfig.getMasterPort());
			ClientHandler.ClientInHandler clientInHandler = new ClientHandler.ClientInHandler();
			clientInHandler.setCluster(cluster);
			clientInHandler.setQueue(localRequestQueue, localResponseQueue);
			transportClient.setClientInHandler(clientInHandler);

		}
		clusterRequestQueue.setTransportClient(transportClient);

	}

	public void start() {
		httpServer.start();
		new Thread(new Runnable() {
			public void run() {
				crawler.start();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				downloader.start();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				distributer.start();
			}
		}).start();
		transportServer.start();
		if (transportClient != null) {
			transportClient.start();
		}
	}

	public void pause() {

	}

	public void resume() {

	}

	public void stop() {
		httpServer.stop();
		crawler.stop();
		downloader.stop();
		transportServer.stop();
		transportClient.stop();
	}

	public void destroy() {
		for (LifeCircle lifeCircle : lifeCircleList) {
			lifeCircle.destroy();
		}
	}
}

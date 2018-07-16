package org.wcong.ants.http.handler;

import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.cluster.Node;
import org.wcong.ants.http.HttpServerHandler;
import org.wcong.ants.transport.TransportMessage;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/29
 */
public class HttpServerSpiderHandler extends HttpServerHandler {

    private Logger logger = LoggerFactory.getLogger(HttpServerSpiderHandler.class);

    public HttpServerSpiderHandler(Node node) {
        this.node = node;
    }

    @Override
    public NettyOutbound handleRequest(HttpServerRequest request, HttpServerResponse response) {
        QueryStringDecoder query = new QueryStringDecoder(request.uri());
        List<String> spider = query.parameters().get("spider");
        if (spider == null || spider.isEmpty()) {
            return sendResponse(response, "none spider");
        }

        node.distributer(TransportMessage.newRequestMessage(null,
                node.getSpiderManager().getSpider(spider.get(0)).getFirstRequests()))
                .subscribe();
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("spider", spider.get(0));
        result.put("time", Calendar.getInstance().getTime());
        return sendResponse(response, result);
    }

    @Override
    public boolean test(HttpServerRequest request) {
        return request.uri().startsWith("/spider?");
    }
}

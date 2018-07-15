package org.wcong.ants.http.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.http.HttpServerHandler;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/28
 */
public class HttpServerSpidersHandler extends HttpServerHandler {

    private static Logger logger = LoggerFactory.getLogger(HttpServerSpidersHandler.class);

    @Override
    public NettyOutbound handleRequest(HttpServerRequest request, HttpServerResponse response) {
        return sendResponse(response, node.getSpiderManager().getSpiderNames());
    }

    @Override
    public boolean test(HttpServerRequest request) {
        return request.uri().equals("/spiders");
    }
}

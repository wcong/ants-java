package org.wcong.ants.http.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.http.HttpServerHandler;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/29
 */
public class HttpServerDefaultHandler extends HttpServerHandler {
    private static Logger logger = LoggerFactory.getLogger(HttpServerDefaultHandler.class);

    private Map<String, Object> welcome = new HashMap<String, Object>();

    {
        welcome.put("message", "fow crawl");
        welcome.put("greeting", "do not panic");
    }

    @Override
    public NettyOutbound handleRequest(HttpServerRequest request, HttpServerResponse response) {
        return sendResponse(response, welcome);
    }

    @Override
    public boolean test(HttpServerRequest request) {
        return request.uri().equals("/");
    }
}

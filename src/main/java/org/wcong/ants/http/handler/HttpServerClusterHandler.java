package org.wcong.ants.http.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.cluster.Cluster;
import org.wcong.ants.http.HttpServerHandler;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/2/2
 */
public class HttpServerClusterHandler extends HttpServerHandler {

    private static Logger logger = LoggerFactory.getLogger(HttpServerClusterHandler.class);

    @Override
    public NettyOutbound handleRequest(HttpServerRequest request, HttpServerResponse response) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        Cluster cluster = node.getCluster();
        dataMap.put("nodes", new ArrayList<>(cluster.getNodeConfigs()));
        dataMap.put("masterNode", cluster.getMasterNode());
        dataMap.put("localNode", cluster.getLocalNode());
        return sendResponse(response, dataMap);
    }

    @Override
    public boolean test(HttpServerRequest request) {
        return request.uri().equals("/cluster");
    }
}

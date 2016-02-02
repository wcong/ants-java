package org.wcong.ants.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.cluster.Cluster;
import org.wcong.ants.cluster.NodeConfig;
import org.wcong.ants.http.HttpServerHandler;

import java.io.IOException;
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
	public void handleRequest(ChannelHandlerContext ctx, HttpRequest request, QueryStringDecoder query,
			HttpContent content) {
		releaseContent(content);
		byte[] data = null;
		try {
			Map<String, Object> dataMap = new HashMap<String, Object>();
			Cluster cluster = node.getCluster();
			dataMap.put("nodes", new ArrayList<NodeConfig>(cluster.getNodeConfigs()));
			dataMap.put("masterNode", cluster.getMasterNode());
			dataMap.put("localNode", cluster.getLocalNode());
			data = objectMapper.writeValueAsBytes(dataMap);
		} catch (IOException e) {
			logger.error("encode error", e);
		}
		if (data == null) {
			data = new byte[0];
		}
		sendResponse(ctx, request, data);

	}

	@Override
	public String getHandlerUri() {
		return "/cluster";
	}
}

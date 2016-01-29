package org.wcong.ants.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.http.HttpServerHandler;

import java.io.IOException;
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

	private String handleUri;

	public HttpServerSpiderHandler() {
		handleUri = "/spider";
	}

	@Override
	public void handleRequest(ChannelHandlerContext ctx, HttpRequest request, QueryStringDecoder query,
			HttpContent content) {
		releaseContent(content);
		List<String> spider = query.parameters().get("spider");
		if (spider == null || spider.isEmpty()) {
			return;
		}
		node.getSpiderManager().startSpider(spider.get(0));
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("spider", spider.get(0));
		result.put("time", Calendar.getInstance().getTime());
		byte[] data = null;
		try {
			data = objectMapper.writeValueAsBytes(result);
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
		return handleUri;
	}
}

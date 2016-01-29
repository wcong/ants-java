package org.wcong.ants.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.http.HttpServerHandler;

import java.io.IOException;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/28
 */
public class HttpServerSpidersHandler extends HttpServerHandler {

	private static Logger logger = LoggerFactory.getLogger(HttpServerSpidersHandler.class);

	private String handleUri;

	public HttpServerSpidersHandler() {
		handleUri = "/spiders";
	}

	@Override
	public void handleRequest(ChannelHandlerContext ctx, HttpRequest request, QueryStringDecoder query,
			HttpContent content) {
		releaseContent(content);
		byte[] data = null;
		try {
			data = objectMapper.writeValueAsBytes(node.getSpiderManager().getSpiderNames());
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

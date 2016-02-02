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
	public void handleRequest(ChannelHandlerContext ctx, HttpRequest request, QueryStringDecoder query,
			HttpContent content) {
		releaseContent(content);
		welcome.put("time", Calendar.getInstance().getTime());
		byte[] data = null;
		try {
			data = objectMapper.writeValueAsBytes(welcome);
		} catch (IOException e) {
			logger.error("json encode error", e);
		}
		if (data == null) {
			data = new byte[0];
		}
		sendResponse(ctx, request, data);
	}

	@Override
	public String getHandlerUri() {
		return "";
	}
}

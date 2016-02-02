package org.wcong.ants.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.cluster.Node;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * about node
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/01/28
 */
public abstract class HttpServerHandler {

	private static Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

	protected ObjectMapper objectMapper = new ObjectMapper();

	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		objectMapper.setDateFormat(df);

	}

	protected Node node;

	public void setNode(Node node) {
		this.node = node;
	}

	public abstract void handleRequest(ChannelHandlerContext ctx, HttpRequest request, QueryStringDecoder query,
			HttpContent content);

	public abstract String getHandlerUri();

	protected void releaseContent(HttpContent content) {
		ByteBuf buf = content.content();
		if (buf.readableBytes() > 0) {
			logger.info("release buf {}", buf.toString(io.netty.util.CharsetUtil.UTF_8));
		}
		buf.release();
	}

	protected void sendResponse(ChannelHandlerContext ctx, HttpRequest request, byte[] data) {
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(data));
		response.headers().set(CONTENT_TYPE, "text/plain");
		response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
		if (HttpHeaders.isKeepAlive(request)) {
			response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		}
		ctx.write(response);
		ctx.flush();
	}
}

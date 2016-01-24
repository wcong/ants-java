package org.wcong.ants.http.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/24
 */
public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(HttpServerInboundHandler.class);

	private HttpRequest request;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			request = (HttpRequest) msg;

			String uri = request.getUri();
			logger.info("Uri:" + uri);
		}
		if (msg instanceof HttpContent) {
			HttpContent content = (HttpContent) msg;
			ByteBuf buf = content.content();
			logger.info(buf.toString(io.netty.util.CharsetUtil.UTF_8));
			buf.release();

			String res = "I am OK";
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK,
					Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
			response.headers().set(CONTENT_TYPE, "text/plain");
			response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
			if (HttpHeaders.isKeepAlive(request)) {
				response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
			}
			ctx.write(response);
			ctx.flush();
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error(cause.getMessage());
		ctx.close();
	}
}

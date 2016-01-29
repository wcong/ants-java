package org.wcong.ants.transport;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/25
 */
public class ClientHandler {

	public static class ClientInHandler extends ChannelInboundHandlerAdapter {

		private static Logger logger = LoggerFactory.getLogger(ClientInHandler.class);

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			logger.info("get meg " + msg);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			logger.error("error", cause);
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			ChannelFuture f = ctx.writeAndFlush("abcd");
			logger.info("write abcd");
		}
	}

	public static class ClientOutHandler extends ChannelOutboundHandlerAdapter {

		private static Logger logger = LoggerFactory.getLogger(ClientOutHandler.class);

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
			ctx.write(msg, promise);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			logger.error("error", cause);
		}
	}
}

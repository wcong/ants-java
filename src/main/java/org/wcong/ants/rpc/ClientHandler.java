package org.wcong.ants.rpc;

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
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			ChannelFuture f = ctx.writeAndFlush("abcd");
			logger.info("write abcd");
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public static class ClientOutHandler extends ChannelOutboundHandlerAdapter {
		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
			ctx.write("abcd", promise);
		}
	}
}

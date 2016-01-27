package org.wcong.ants.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/25
 */
public class ServerHandler {

	public static class ServerInHandler extends ChannelInboundHandlerAdapter {

		private static Logger logger = LoggerFactory.getLogger(ServerInHandler.class);

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			logger.info("get message " + msg);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			logger.error("get error", cause);
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			Server.addChannel(ctx.channel());
		}
	}

	public static class ServerOutHandler extends ChannelOutboundHandlerAdapter {

		private static Logger logger = LoggerFactory.getLogger(ServerOutHandler.class);

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
			ctx.write(msg, promise);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			logger.error("get error", cause);
		}
	}

}

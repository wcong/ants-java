package org.wcong.ants.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/25
 */
public class ServerHandler {

	public static class ServerInHandler extends ChannelInboundHandlerAdapter {
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			System.out.print(msg);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

		}
	}

}

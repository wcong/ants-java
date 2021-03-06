package org.wcong.ants.transport;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/25
 */
public class MessageEncoder extends MessageToByteEncoder {
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
		out.writeBytes(Codec.codec.encode(msg));
	}
}

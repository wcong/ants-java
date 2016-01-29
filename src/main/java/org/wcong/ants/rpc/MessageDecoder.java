package org.wcong.ants.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.rpc.support.DefaultCodec;

import java.util.List;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/25
 */
public class MessageDecoder extends ByteToMessageDecoder {

	private static Logger logger = LoggerFactory.getLogger(MessageDecoder.class);

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		logger.info("coming message " + in.readableBytes());
		Object object = Codec.codec.decode(in);
		if (object != null) {
			out.add(object);
		}
	}
}

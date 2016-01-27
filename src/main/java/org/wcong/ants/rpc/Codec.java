package org.wcong.ants.rpc;

import io.netty.buffer.ByteBuf;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/26
 */
public interface Codec {

	Object decode(ByteBuf in);

	byte[] encode(Object object);

}

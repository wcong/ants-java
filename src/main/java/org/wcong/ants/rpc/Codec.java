package org.wcong.ants.rpc;

import io.netty.buffer.ByteBuf;
import org.wcong.ants.rpc.support.DefaultCodec;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/26
 */
public interface Codec {

	Codec codec = new DefaultCodec();

	Object decode(ByteBuf in);

	byte[] encode(Object object);

}

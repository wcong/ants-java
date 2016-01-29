package org.wcong.ants.transport;

import io.netty.buffer.ByteBuf;
import org.wcong.ants.transport.support.DefaultCodec;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/26
 */
public interface Codec {

	Codec codec = new DefaultCodec();

	Object decode(ByteBuf in);

	byte[] encode(Object object);

}

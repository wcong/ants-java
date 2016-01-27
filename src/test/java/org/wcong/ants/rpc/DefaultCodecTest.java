package org.wcong.ants.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.Test;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/26
 */
public class DefaultCodecTest {

	@Test
	public void testEncodeAndDecode() {
		DefaultCodec defaultCodec = new DefaultCodec();
		byte[] encode = defaultCodec.encode("abced");
		ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer(encode.length);
		byteBuf.writeBytes(encode);
		byteBuf.writeBytes("sdad".getBytes());
		String decodeString = (String) defaultCodec.decode(byteBuf);
		System.out.println(decodeString.equals("abced"));
	}

}

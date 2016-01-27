package org.wcong.ants.util;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/26
 */
public class ByteUtils {

	public static byte[] IntToByteArray(int num) {
		byte[] byteArray = new byte[4];
		byteArray[3] = (byte) (num & 0xff);
		num >>= 8;
		byteArray[2] = (byte) (num & 0xff);
		num >>= 8;
		byteArray[1] = (byte) (num & 0xff);
		num >>= 8;
		byteArray[0] = (byte) num;
		return byteArray;
	}

}

package org.wcong.ants.rpc;

import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.util.ByteUtils;

import java.io.*;
import java.util.Arrays;

/**
 * default code for java Serializable
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/26
 */
public class DefaultCodec implements Codec {

	public static Codec codec = new DefaultCodec();

	private static Logger logger = LoggerFactory.getLogger(DefaultCodec.class);

	private static byte[] MARK = "ants".getBytes();

	private static int INT_LENGTH = Integer.SIZE / Byte.SIZE;

	private static int DATA_INDEX = MARK.length + INT_LENGTH;

	public Object decode(ByteBuf in) {
		if (in.readableBytes() <= DATA_INDEX) {
			return null;
		}
		int readIndex = in.readerIndex();
		byte[] mark = new byte[MARK.length];
		in.readBytes(mark, 0, MARK.length);
		if (!markRight(mark)) {
			in.clear();
			return null;
		}
		int objectLength = in.readInt();
		if (in.readableBytes() < objectLength) {
			in.readerIndex(readIndex);
			return null;
		} else {
			byte[] objectBytes = new byte[objectLength];
			in.readBytes(objectBytes, 0, objectBytes.length);
			return readObject(objectBytes);
		}
	}

	private Object readObject(byte[] bytes) {
		ObjectInputStream objectInputStream = null;
		Object object = null;
		try {
			objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
			object = objectInputStream.readObject();
		} catch (Exception e) {
			logger.error("read object error", e);
		} finally {
			if (objectInputStream != null) {
				try {
					objectInputStream.close();
				} catch (IOException e) {
					logger.error("close object reader error", e);
				}
			}
		}
		return object;
	}

	private boolean markRight(byte[] mark) {
		return Arrays.equals(mark, MARK);
	}

	public byte[] encode(Object object) {
		if (object == null) {
			return null;
		}
		byte[] encodeResult = null;
		ObjectOutputStream objectOutputStream = null;
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			byte[] objectByteArray = byteArrayOutputStream.toByteArray();
			encodeResult = writeBytes(objectByteArray);
			objectOutputStream.close();
		} catch (IOException e) {
			logger.error("encode error", e);
		} finally {
			if (objectOutputStream != null) {
				try {
					objectOutputStream.close();
				} catch (IOException e) {
					logger.error("close stream error", e);
				}
			}
		}
		return encodeResult;
	}

	private byte[] writeBytes(byte[] objectByteArray) {
		if (objectByteArray == null) {
			return null;
		}
		byte[] lengthArray = ByteUtils.IntToByteArray(objectByteArray.length);
		byte[] encodeResult = new byte[MARK.length + INT_LENGTH + objectByteArray.length];
		int index = 0;
		System.arraycopy(MARK, 0, encodeResult, index, MARK.length);
		index += MARK.length;
		System.arraycopy(lengthArray, 0, encodeResult, index, lengthArray.length);
		index += lengthArray.length;
		System.arraycopy(objectByteArray, 0, encodeResult, index, objectByteArray.length);
		return encodeResult;
	}
}

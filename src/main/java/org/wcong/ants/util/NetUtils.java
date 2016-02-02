package org.wcong.ants.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/30
 */
public class NetUtils {

	private static Logger logger = LoggerFactory.getLogger(NetUtils.class);

	public static String getLocalIp() {
		try {
			return Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.error("get local ip error", e);
		}
		return null;
	}

}

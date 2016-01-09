package org.wcong.ants.downloader.support.netty;

import io.netty.channel.ChannelFuture;

import java.util.HashMap;
import java.util.Map;

/**
 * client pool of netty
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/26
 */
public class NettyHttpClientPool {

	private static Map<String, ChannelFuture> clientMap = new HashMap<String, ChannelFuture>();

	public static ChannelFuture getClient(String host) {
		ChannelFuture client = clientMap.get(host);
		if (client == null) {
			synchronized (NettyHttpClientPool.class) {
				client = clientMap.get(host);
				if (client == null) {
					clientMap.put(host, client);
				}
			}
		}
		return client;
	}

}

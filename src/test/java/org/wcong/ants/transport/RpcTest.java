package org.wcong.ants.transport;

import org.junit.Test;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/25
 */
public class RpcTest {

	@Test
	public void test() throws InterruptedException {
		final TransportClient client = new TransportClient();
		final TransportServer server = new TransportServer();
		server.run();
		client.run();
		client.getChannel().writeAndFlush("haha").sync();
		TransportServer.getChannel("").writeAndFlush("hehe").sync();
		Thread.sleep(1000);
	}

}

package org.wcong.ants.rpc;

import org.junit.Test;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/25
 */
public class RpcTest {

	@Test
	public void test() throws InterruptedException {
		final Client client = new Client();
		final Server server = new Server();
		server.run();
		client.run();
		client.getChannel().writeAndFlush("haha").sync();
		Server.getChannel("").writeAndFlush("hehe").sync();
		Thread.sleep(1000);
	}

}

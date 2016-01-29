package org.wcong.ants.http;

import org.junit.Test;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/24
 */
public class HttpServerTest {

	@Test
	public void testServer() {
		HttpServer httpServer = new HttpServer();
		httpServer.start();
		httpServer.stop();
	}

}

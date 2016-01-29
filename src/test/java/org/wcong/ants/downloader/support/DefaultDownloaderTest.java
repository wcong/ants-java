package org.wcong.ants.downloader.support;

import org.junit.Test;
import org.wcong.ants.downloader.Request;
import org.wcong.ants.downloader.Response;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/23
 */
public class DefaultDownloaderTest {

	@Test
	public void testDownloader() {
		DefaultDownloader defaultDownloader = new DefaultDownloader();
		BlockingDeque<Request> requestBlockingDeque = new LinkedBlockingDeque<Request>();
		Request request = new Request();
		requestBlockingDeque.add(request);
		request.setParseName("test");
		request.setNodeName("test");
		request.setUrl("http://www.baidu.com");
		defaultDownloader.setQueue(requestBlockingDeque, new LinkedBlockingDeque<Response>());
		defaultDownloader.run();
		defaultDownloader.stop();
	}

}

package org.wcong.ants.crawler.support;

import org.junit.Test;
import org.wcong.ants.crawler.Spider;
import org.wcong.ants.downloader.Request;
import org.wcong.ants.downloader.Response;
import org.wcong.ants.downloader.support.DefaultDownloader;
import org.wcong.ants.spider.DouBanSpider;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/23
 */
public class DefaultCrawlerTest {

	@Test
	public void testCrawler() throws InterruptedException {
		BlockingDeque<Request> requestBlockingDeque = new LinkedBlockingDeque<Request>();
		BlockingDeque<Response> responseBlockingDeque = new LinkedBlockingDeque<Response>();
		final DefaultDownloader defaultDownloader = new DefaultDownloader();
		final DefaultCrawler defaultCrawler = new DefaultCrawler();
		defaultDownloader.init(requestBlockingDeque, responseBlockingDeque);
		defaultCrawler.init(requestBlockingDeque, responseBlockingDeque);
		Spider spider = new DouBanSpider("DouBan");
		spider.init();
		requestBlockingDeque.addAll(spider.getFirstRequests());
		defaultCrawler.addSpider(spider);
		new Thread(new Runnable() {
			public void run() {
				defaultDownloader.run();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				defaultCrawler.run();
			}
		}).start();
		Thread.sleep(5000);
		defaultDownloader.stop();
		Thread.sleep(5000);
		defaultCrawler.stop();
	}

}

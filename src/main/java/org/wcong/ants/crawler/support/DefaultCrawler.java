package org.wcong.ants.crawler.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.Status;
import org.wcong.ants.cluster.ClusterRequestBlockingQueue;
import org.wcong.ants.crawler.Crawler;
import org.wcong.ants.crawler.Parser;
import org.wcong.ants.crawler.Result;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.RequestBlockingQueue;
import org.wcong.ants.spider.Response;
import org.wcong.ants.spider.ResponseBlockingQueue;
import org.wcong.ants.spider.Spider;
import org.wcong.ants.spider.SpiderManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/23
 */
public class DefaultCrawler implements Crawler {

	private static Logger logger = LoggerFactory.getLogger(DefaultCrawler.class);

	private SpiderManager spiderManager;

	private ClusterRequestBlockingQueue clusterRequests;

	private ResponseBlockingQueue responses;

	private ExecutorService crawlThreadPool = Executors.newFixedThreadPool(16);

	private volatile Status status = Status.NONE;

	public void crawl(final Response response) {
		if (response == null) {
			logger.warn("null parameter");
			return;
		}
		final Request request = response.getRequest();
		if (request == null) {
			logger.warn("null request for response {}", response);
			return;
		}
		Spider spider = spiderManager.getSpider(request.getSpiderName());
		if (spider == null) {
			logger.warn("none spider for response {}", response);
			return;
		}
		final Parser parser = spider.getParser(request.getParseName());
		if (parser == null) {
			logger.warn("none parser for response {}", response);
			return;
		}
		logger.info("start to crawl response");
		crawlThreadPool.submit(new Runnable() {
			public void run() {
				try {
					Result result = parser.parse(response);
					logger.info("crawled response");
					if (result != null && result.getRequestList() != null) {
						logger.info("push request to queue {}", result.getRequestList());
						clusterRequests.addToCluster(response.getRequest(), result.getRequestList());
					}
				} catch (Exception e) {
					logger.error("crawl exception for response", e);
				}
			}
		});
	}

	public void run() {
		status = Status.RUNNING;
		try {
			Response response;
			while (status == Status.RUNNING) {
				if ((response = responses.poll(1, TimeUnit.SECONDS)) != null) {
					crawl(response);
				}
			}
		} catch (Exception e) {
			logger.error("error happen ", e);
		}
		crawlThreadPool.shutdown();
	}

	public void init() {

	}

	public void start() {
		run();
	}

	public void pause() {

	}

	public void resume() {

	}

	public void stop() {
		status = Status.STOP;
	}

	public void destroy() {

	}

	public void setSpiderManager(SpiderManager spiderManager) {
		this.spiderManager = spiderManager;
	}

	public void setClusterQueue(ClusterRequestBlockingQueue clusterRequestBlockingQueue) {
		this.clusterRequests = clusterRequestBlockingQueue;
	}

	public void setQueue(RequestBlockingQueue requests, ResponseBlockingQueue responses) {
		this.responses = responses;
	}
}

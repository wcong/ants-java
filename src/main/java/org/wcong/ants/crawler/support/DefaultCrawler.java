package org.wcong.ants.crawler.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.Status;
import org.wcong.ants.crawler.Crawler;
import org.wcong.ants.crawler.Parser;
import org.wcong.ants.crawler.Result;
import org.wcong.ants.crawler.Spider;
import org.wcong.ants.downloader.Request;
import org.wcong.ants.downloader.Response;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/23
 */
public class DefaultCrawler implements Crawler {

	private static Logger logger = LoggerFactory.getLogger(DefaultCrawler.class);

	private Map<String, Spider> spiderMap = new ConcurrentHashMap<String, Spider>();

	private BlockingQueue<Request> requests;

	private BlockingQueue<Response> responses;

	private ExecutorService crawlThreadPool = Executors.newFixedThreadPool(16);

	private volatile Status status = Status.NONE;

	public void init(BlockingQueue<Request> requests, BlockingQueue<Response> responses) {
		this.requests = requests;
		this.responses = responses;
	}

	public void addSpider(Spider spider) {
		spiderMap.put(spider.getName(), spider);
	}

	public void removeSpider(Spider spider) {
		spiderMap.remove(spider.getName());
	}

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
		Spider spider = spiderMap.get(request.getSpiderName());
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
				Result result = parser.parse(response);
				logger.info("crawled response");
				if (result != null && result.getRequestList() != null) {
					logger.info("push request to queue {}", result.getRequestList());
					requests.addAll(result.getRequestList());
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

	public void stop() {
		status = Status.STOP;
	}

}

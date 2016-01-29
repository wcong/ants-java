package org.wcong.ants.cluster.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.cluster.Node;
import org.wcong.ants.cluster.NodeConfig;
import org.wcong.ants.crawler.Crawler;
import org.wcong.ants.crawler.support.DefaultCrawler;
import org.wcong.ants.downloader.Downloader;
import org.wcong.ants.downloader.Request;
import org.wcong.ants.downloader.Response;
import org.wcong.ants.downloader.support.DefaultDownloader;
import org.wcong.ants.http.HttpServer;
import org.wcong.ants.spider.SpiderManager;
import org.wcong.ants.spider.support.DefaultSpiderManager;
import org.wcong.ants.transport.TransportClient;
import org.wcong.ants.transport.TransportServer;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * a node
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/28
 */
public class DefaultNode implements Node {

	private static Logger logger = LoggerFactory.getLogger(DefaultNode.class);

	private NodeConfig nodeConfig;

	private HttpServer httpServer = new HttpServer();

	private SpiderManager spiderManager = new DefaultSpiderManager();

	private Crawler crawler = new DefaultCrawler();

	private Downloader downloader = new DefaultDownloader();

	private TransportClient transportClient = new TransportClient();

	private TransportServer transportServer = new TransportServer();

	public void setNodeConfig(NodeConfig nodeConfig) {
		this.nodeConfig = nodeConfig;
	}

	public SpiderManager getSpiderManager() {
		return spiderManager;
	}

	public void init() {
		spiderManager.loadSpider(nodeConfig.getSpiderPackages());
		BlockingDeque<Request> requestBlockingDeque = new LinkedBlockingDeque<Request>();
		BlockingDeque<Response> responseBlockingDeque = new LinkedBlockingDeque<Response>();
		spiderManager.setQueue(requestBlockingDeque, responseBlockingDeque);
		crawler.setQueue(requestBlockingDeque, responseBlockingDeque);
		crawler.setSpiderManager(spiderManager);
		downloader.setQueue(requestBlockingDeque, responseBlockingDeque);
		httpServer.setNode(this);
		transportServer.setPort(nodeConfig.getTcpPort());
	}

	public void start() {
		httpServer.start();
		new Thread(new Runnable() {
			public void run() {
				crawler.start();
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				downloader.start();
			}
		}).start();
	}

	public void pause() {

	}

	public void resume() {

	}

	public void stop() {
		httpServer.stop();
		crawler.stop();
		downloader.stop();
	}

	public void destroy() {

	}
}

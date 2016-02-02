package org.wcong.ants.spider.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.cluster.ClusterRequestBlockingQueue;
import org.wcong.ants.spider.Spider;
import org.wcong.ants.spider.SpiderManager;
import org.wcong.ants.util.ClassScanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * manage spider
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/29
 */
public class DefaultSpiderManager implements SpiderManager {

	private static Logger logger = LoggerFactory.getLogger(DefaultSpiderManager.class);

	private Map<String, Spider> spiderMap = new HashMap<String, Spider>();

	private ClusterRequestBlockingQueue requests;

	public void loadSpider(List<String> spiderPackages) {
		List<Class<Spider>> spiderClassList = ClassScanner
				.scanPackages(Spider.class, spiderPackages.toArray(new String[spiderPackages.size()]));
		for (Class<Spider> spiderClass : spiderClassList) {
			Spider spider = null;
			try {
				spider = spiderClass.newInstance();
				spider.init();
			} catch (Exception e) {
				logger.error("new Spider error", e);
			}
			if (spider != null) {
				spiderMap.put(spider.getName(), spider);
			}
		}
	}

	public Spider getSpider(String name) {
		return spiderMap.get(name);
	}

	public List<String> getSpiderNames() {
		return new ArrayList<String>(spiderMap.keySet());
	}

	public void startSpider(String name) {
		Spider spider = spiderMap.get(name);
		if (spider == null) {
			return;
		}
		logger.info("start spider {}", name);
		requests.addToCluster(null, spider.getFirstRequests());
	}

	public void setClusterQueue(ClusterRequestBlockingQueue requests) {
		this.requests = requests;
	}
}

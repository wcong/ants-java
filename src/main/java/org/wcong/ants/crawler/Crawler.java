package org.wcong.ants.crawler;

import org.wcong.ants.downloader.Response;

/**
 * a crawler
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/24
 */
public interface Crawler {

	void addSpider(Spider spider);

	void removeSpider(Spider spider);

	void crawl(Response response);

}

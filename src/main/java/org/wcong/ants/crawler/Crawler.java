package org.wcong.ants.crawler;

import org.wcong.ants.LifeCircle;
import org.wcong.ants.QueueInit;
import org.wcong.ants.SpiderManagerInit;
import org.wcong.ants.downloader.Response;

/**
 * a crawler
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/24
 */
public interface Crawler extends LifeCircle, QueueInit, SpiderManagerInit {

	void crawl(Response response);

}

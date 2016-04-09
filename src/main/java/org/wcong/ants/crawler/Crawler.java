package org.wcong.ants.crawler;

import org.wcong.ants.LifeCircle;
import org.wcong.ants.aware.ClusterQueueAware;
import org.wcong.ants.aware.QueueAware;
import org.wcong.ants.aware.SpiderManagerAware;
import org.wcong.ants.index.DocumentWriter;
import org.wcong.ants.spider.Response;

/**
 * a crawler
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/24
 */
public interface Crawler extends LifeCircle, QueueAware, SpiderManagerAware, ClusterQueueAware {

	void crawl(Response response);

	void setDocumentWriter(DocumentWriter docunemtWriter);

}

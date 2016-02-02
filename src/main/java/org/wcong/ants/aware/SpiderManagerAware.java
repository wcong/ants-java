package org.wcong.ants.aware;

import org.wcong.ants.spider.SpiderManager;

/**
 * init spider manager
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/29
 */
public interface SpiderManagerAware {

	void setSpiderManager(SpiderManager spiderManager);

}

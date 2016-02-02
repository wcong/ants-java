package org.wcong.ants.downloader;

import org.wcong.ants.LifeCircle;
import org.wcong.ants.aware.QueueAware;
import org.wcong.ants.spider.Request;

/**
 * download html
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/18
 */
public interface Downloader extends LifeCircle, QueueAware {

	void download(Request request) throws Exception;

}

package org.wcong.ants.aware;

import org.wcong.ants.spider.RequestBlockingQueue;
import org.wcong.ants.spider.ResponseBlockingQueue;

/**
 * init queue
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/29
 */
public interface QueueAware {

	void setQueue(RequestBlockingQueue requests, ResponseBlockingQueue responses);

}

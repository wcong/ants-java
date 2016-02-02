package org.wcong.ants.spider;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/2/1
 */
public interface ResponseBlockingQueue {

	Response poll(long timeout, TimeUnit unit) throws InterruptedException;

	boolean addWaiting(Collection<? extends Response> c);

	boolean addCrawling(Collection<? extends Response> c);

	boolean addCrawled(Collection<? extends Response> c);

	boolean removeCrawling(Collection<? extends Response> c);

	int crawlingNum();

}

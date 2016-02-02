package org.wcong.ants.spider;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * request queue
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/2/1
 */
public interface RequestBlockingQueue {

	Request poll(long timeout, TimeUnit unit) throws InterruptedException;

	boolean addWaiting(Collection<? extends Request> c);

	boolean addDownloading(Collection<? extends Request> c);

	boolean addDownloaded(Collection<? extends Request> c);

	boolean removeDownloaded(Collection<? extends Request> c);

	int downloadingNum();
}

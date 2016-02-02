/**
 * Copyright 2014-2015, NetEase, Inc. All Rights Reserved.
 * Date: 16/2/1
 */
package org.wcong.ants.spider.support;

import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.RequestBlockingQueue;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/2/1
 */
public class LinkedRequestBlockingQueue implements RequestBlockingQueue {

	private LinkedBlockingDeque<Request> waitingQueue = new LinkedBlockingDeque<Request>();

	private LinkedBlockingDeque<Request> downloadedQueue = new LinkedBlockingDeque<Request>();

	private Map<String, Request> crawlingRequest = new ConcurrentHashMap<String, Request>();

	public Request poll(long timeout, TimeUnit unit) throws InterruptedException {
		Request request = waitingQueue.poll(timeout, unit);
		if (request != null) {
			addDownloading(Collections.singleton(request));
		}
		return request;
	}

	public boolean addWaiting(Collection<? extends Request> c) {
		return waitingQueue.addAll(c);
	}

	public boolean addDownloading(Collection<? extends Request> c) {
		if (c == null) {
			return false;
		}
		for (Request request : c) {
			crawlingRequest.put(request.getId(), request);
		}
		return true;
	}

	public boolean addDownloaded(Collection<? extends Request> c) {
		removeDownloaded(c);
		return downloadedQueue.addAll(c);
	}

	public boolean removeDownloaded(Collection<? extends Request> c) {
		if (c == null) {
			return false;
		}
		for (Request request : c) {
			crawlingRequest.remove(request.getId());
		}
		return true;
	}

	public int downloadingNum() {
		return crawlingRequest.size();
	}
}

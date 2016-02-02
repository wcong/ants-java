package org.wcong.ants.spider.support;

import org.wcong.ants.spider.Response;
import org.wcong.ants.spider.ResponseBlockingQueue;
import org.wcong.ants.util.IdGenerate;

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
public class LinkedResponseBlockingQueue implements ResponseBlockingQueue {

	private LinkedBlockingDeque<Response> waitingQueue = new LinkedBlockingDeque<Response>();

	private LinkedBlockingDeque<Response> crawledQueue = new LinkedBlockingDeque<Response>();

	private Map<String, Response> crawlingRequest = new ConcurrentHashMap<String, Response>();

	public Response poll(long timeout, TimeUnit unit) throws InterruptedException {
		Response response = waitingQueue.poll(timeout, unit);
		if (response != null) {
			addCrawling(Collections.singleton(response));
		}
		return response;
	}

	public boolean addWaiting(Collection<? extends Response> c) {
		generateId(c);
		return waitingQueue.addAll(c);
	}

	public boolean addCrawling(Collection<? extends Response> c) {
		if (c == null) {
			return false;
		}
		for (Response response : c) {
			crawlingRequest.put(response.getId(), response);
		}
		return true;
	}

	public boolean addCrawled(Collection<? extends Response> c) {
		removeCrawling(c);
		return crawledQueue.addAll(c);
	}

	public boolean removeCrawling(Collection<? extends Response> c) {
		if (c == null) {
			return false;
		}
		for (Response response : c) {
			crawlingRequest.remove(response.getId());
		}
		return true;
	}

	public int crawlingNum() {
		return crawlingRequest.size();
	}

	private void generateId(Collection<? extends Response> c) {
		if (c == null) {
			return;
		}
		for (Response response : c) {
			response.setId(IdGenerate.generate(response));
		}
	}
}

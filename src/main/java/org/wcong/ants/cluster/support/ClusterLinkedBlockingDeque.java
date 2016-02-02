package org.wcong.ants.cluster.support;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.cluster.Cluster;
import org.wcong.ants.cluster.ClusterRequestBlockingQueue;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.RequestBlockingQueue;
import org.wcong.ants.spider.ResponseBlockingQueue;
import org.wcong.ants.transport.TransportClient;
import org.wcong.ants.transport.TransportMessage;
import org.wcong.ants.transport.TransportServer;
import org.wcong.ants.transport.message.CrawlResult;
import org.wcong.ants.util.IdGenerate;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * request queue for cluster
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/30
 */
public class ClusterLinkedBlockingDeque implements ClusterRequestBlockingQueue {

	private static Logger logger = LoggerFactory.getLogger(ClusterLinkedBlockingDeque.class);

	private RequestBlockingQueue localRequests;

	private Cluster cluster;

	private TransportClient transportClient;

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

	/**
	 * generate id and send it to a node
	 *
	 * @param c request List
	 * @return is ok
	 */
	public boolean addToCluster(Request request, Collection<? extends Request> c) {
		if (c == null) {
			return false;
		}
		makeNodeName(c);
		makeRequestId(c);
		if (cluster.isLocalMaster()) {
			if (request != null) {
				addDownloaded(Collections.singleton(request));
			}
			return addWaiting(c);
		} else {
			try {
				CrawlResult crawlResult = CrawlResult.newInstance(request, c);
				TransportMessage transportMessage = TransportMessage
						.newRequestMessage(cluster.getMasterNode().getNodeName(), crawlResult);
				transportClient.getChannel().writeAndFlush(transportMessage).sync();
				return true;
			} catch (InterruptedException e) {
				logger.error("send server request error", e);
				return false;
			}
		}
	}

	public boolean addToSlave(Collection<? extends Request> c, String nodeName) {
		if (cluster.getLocalNode().getNodeName().equals(nodeName)) {
			return localRequests.addWaiting(c);
		} else {
			try {
				Channel channel = TransportServer.getChannel(nodeName);
				TransportMessage transportMessage = TransportMessage
						.newRequestMessage(cluster.getLocalNode().getNodeName(), c);
				channel.writeAndFlush(transportMessage).sync();
				return true;
			} catch (InterruptedException e) {
				logger.error("send server request error", e);
				return false;
			}
		}
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

	private void makeRequestId(Collection<? extends Request> c) {
		if (c == null) {
			return;
		}
		for (Request request : c) {
			if (request.getId() != null) {
				continue;
			}
			request.setId(IdGenerate.generate(request));
		}
	}

	private void makeNodeName(Collection<? extends Request> c) {
		if (c == null) {
			return;
		}
		String nodeName = cluster.getLocalNode().getNodeName();
		for (Request request : c) {
			request.setNodeName(nodeName);
		}
	}

	public void setTransportClient(TransportClient transportClient) {
		this.transportClient = transportClient;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public void setQueue(RequestBlockingQueue requests, ResponseBlockingQueue responses) {
		localRequests = requests;
	}
}

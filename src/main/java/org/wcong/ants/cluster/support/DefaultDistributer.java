/**
 * Copyright 2014-2015, NetEase, Inc. All Rights Reserved.
 * Date: 16/2/1
 */
package org.wcong.ants.cluster.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.Status;
import org.wcong.ants.cluster.Cluster;
import org.wcong.ants.cluster.ClusterRequestBlockingQueue;
import org.wcong.ants.cluster.Distributer;
import org.wcong.ants.cluster.NodeConfig;
import org.wcong.ants.spider.Request;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @author hzwangcong<hzwangcong@corp.netease.com>
 * @since 16/2/1
 */
public class DefaultDistributer implements Distributer {

	private static Logger logger = LoggerFactory.getLogger(DefaultDistributer.class);

	private volatile Status status = Status.INIT;

	private ClusterRequestBlockingQueue clusterQueue;

	private Cluster cluster;

	private int nodeIndex = 0;

	public void init() {

	}

	public void start() {
		status = Status.RUNNING;
		while (status == Status.RUNNING) {
			Request request = null;
			try {
				request = clusterQueue.poll(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				logger.error("poll error", e);
			}
			if (request != null) {
				NodeConfig nodeConfig = selectConfig();
				clusterQueue.addToSlave(Collections.singleton(request), nodeConfig.getNodeName());
			}
		}
	}

	private NodeConfig selectConfig() {
		if (nodeIndex >= cluster.getNodeConfigs().size()) {
			nodeIndex = 0;
		}
		Iterator<NodeConfig> requestIterator = cluster.getNodeConfigs().iterator();
		NodeConfig nodeConfig = null;
		for (int i = 0; requestIterator.hasNext() && i < nodeIndex; i++) {
			nodeConfig = requestIterator.next();
		}
		nodeIndex += 1;
		if (requestIterator.hasNext()) {
			nodeConfig = requestIterator.next();
		}
		return nodeConfig;
	}

	public void pause() {

	}

	public void resume() {

	}

	public void stop() {
		status = Status.STOP;
	}

	public void destroy() {

	}

	public void setClusterQueue(ClusterRequestBlockingQueue clusterRequestBlockingQueue) {
		this.clusterQueue = clusterRequestBlockingQueue;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}
}

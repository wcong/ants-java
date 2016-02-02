package org.wcong.ants.cluster.support;

import org.wcong.ants.cluster.Cluster;
import org.wcong.ants.cluster.ClusterRequestBlockingQueue;
import org.wcong.ants.cluster.NodeConfig;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/2/1
 */
public class DefaultCluster implements Cluster {

	private BlockingQueue<NodeConfig> nodeConfigList = new LinkedBlockingDeque<NodeConfig>();

	private ClusterRequestBlockingQueue clusterRequestBlockingQueue;

	private NodeConfig masterNode;

	private NodeConfig localNode;

	public Collection<NodeConfig> getNodeConfigs() {
		return nodeConfigList;
	}

	public void addNodeConfig(NodeConfig nodeConfig) {
		if (contains(nodeConfig.getNodeName())) {
			return;
		}
		nodeConfigList.add(nodeConfig);
		if (localNode.getMasterIp().equals(nodeConfig.getLocalIp()) && localNode.getMasterPort() == nodeConfig
				.getTcpPort()) {
			masterNode = nodeConfig;
		}
	}

	public void removeNodeConfig(NodeConfig nodeConfig) {
		Iterator<NodeConfig> nodeConfigIterator = nodeConfigList.iterator();
		while (nodeConfigIterator.hasNext()) {
			if (nodeConfigIterator.next().getNodeName().equals(nodeConfig.getNodeName())) {
				nodeConfigIterator.remove();
			}
		}
	}

	public boolean contains(String nodeName) {
		for (NodeConfig nodeConfig : nodeConfigList) {
			if (nodeConfig.getNodeName().equals(nodeName)) {
				return true;
			}
		}
		return false;
	}

	public ClusterRequestBlockingQueue getClusterBlockingQueue() {
		return clusterRequestBlockingQueue;
	}

	public void setClusterQueue(ClusterRequestBlockingQueue clusterRequestBlockingQueue) {
		this.clusterRequestBlockingQueue = clusterRequestBlockingQueue;
	}

	public NodeConfig getMasterNode() {
		return masterNode;
	}

	public void setMasterNode(NodeConfig masterNode) {
		this.masterNode = masterNode;
	}

	public NodeConfig getLocalNode() {
		return localNode;
	}

	public void setLocalNode(NodeConfig localNode) {
		this.localNode = localNode;
		addNodeConfig(localNode);
	}

	public boolean isLocalMaster() {
		return localNode.getNodeName().equals(masterNode.getNodeName());
	}
}

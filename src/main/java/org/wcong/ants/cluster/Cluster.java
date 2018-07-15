package org.wcong.ants.cluster;

import java.util.Collection;

/**
 * cluster
 * all node configure
 * all request
 * master node
 * this node
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/01/30
 */
public interface Cluster {

    Collection<NodeConfig> getNodeConfigs();

    void addNodeConfig(NodeConfig nodeConfig);

    void removeNodeConfig(NodeConfig nodeConfig);

    boolean contains(String nodeName);

    ClusterRequestBlockingQueue getClusterBlockingQueue();

    NodeConfig getMasterNode();

    void setMasterNode(NodeConfig masterNode);

    NodeConfig getLocalNode();

    void setLocalNode(NodeConfig localNode);

    boolean isLocalMaster();

}

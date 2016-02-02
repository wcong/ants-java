package org.wcong.ants.aware;

import org.wcong.ants.cluster.ClusterRequestBlockingQueue;

/**
 * set  ClusterBlockingQueue
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/2/1
 */
public interface ClusterQueueAware {

	void setClusterQueue(ClusterRequestBlockingQueue clusterRequestBlockingQueue);

}

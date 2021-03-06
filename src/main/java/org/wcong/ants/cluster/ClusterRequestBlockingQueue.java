package org.wcong.ants.cluster;

import org.wcong.ants.aware.ClusterAware;
import org.wcong.ants.aware.QueueAware;
import org.wcong.ants.aware.TransportClientAware;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.RequestBlockingQueue;

import java.util.Collection;

/**
 * about node configure for local use
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/01/30
 */
public interface ClusterRequestBlockingQueue
		extends TransportClientAware, RequestBlockingQueue, ClusterAware, QueueAware {

	boolean addToCluster(Request originRequest, Collection<? extends Request> c);

	boolean addToSlave(Collection<? extends Request> c, String nodeName);

}

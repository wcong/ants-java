package org.wcong.ants.cluster;

import org.wcong.ants.aware.ClusterAware;
import org.wcong.ants.aware.QueueAware;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.RequestBlockingQueue;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;

import java.util.Collection;
import java.util.Map;

/**
 * about node configure for local use
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/01/30
 */
public interface ClusterRequestBlockingQueue
        extends RequestBlockingQueue, ClusterAware, QueueAware {

    boolean addToCluster(Request originRequest, Collection<? extends Request> c);

    Mono<Void> addToSlave(Collection<? extends Request> c, String nodeName);

    void setOutBoundMap(Map<String, NettyOutbound> outBoundMap);

}

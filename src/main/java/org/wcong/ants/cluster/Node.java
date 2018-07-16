package org.wcong.ants.cluster;

import org.wcong.ants.LifeCircle;
import org.wcong.ants.spider.SpiderManager;
import org.wcong.ants.transport.TransportMessage;
import reactor.core.publisher.Flux;

/**
 * about node
 * cluster
 * queue
 * node configure
 * all kind of server
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/01/18
 */
public interface Node extends LifeCircle {

    void setNodeConfig(NodeConfig nodeConfig);

    SpiderManager getSpiderManager();

    String getName();

    Cluster getCluster();

    Flux<Void> distributer(TransportMessage message);

}

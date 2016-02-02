package org.wcong.ants.cluster;

import org.wcong.ants.LifeCircle;
import org.wcong.ants.aware.ClusterAware;
import org.wcong.ants.aware.ClusterQueueAware;

/**
 * distribute  request
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/01/30
 */
public interface Distributer extends LifeCircle, ClusterQueueAware, ClusterAware {
}

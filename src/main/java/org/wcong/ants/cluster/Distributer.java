package org.wcong.ants.cluster;

import org.wcong.ants.LifeCircle;
import org.wcong.ants.aware.ClusterAware;

/**
 * distribute  request
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/01/30
 */
public interface Distributer extends LifeCircle, ClusterAware {
    NodeConfig selectConfig();
}

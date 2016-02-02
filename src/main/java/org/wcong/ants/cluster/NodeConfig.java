package org.wcong.ants.cluster;

import java.io.Serializable;

/**
 * about node configure
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/01/18
 */
public interface NodeConfig extends LocalNodeConfig, RemoteNodeConfig, Serializable {

	boolean isLocalMaster();

}

package org.wcong.ants.cluster;

/**
 * about node configure for remote use
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/01/30
 */
public interface RemoteNodeConfig {

	String getMasterIp();

	int getMasterPort();
}

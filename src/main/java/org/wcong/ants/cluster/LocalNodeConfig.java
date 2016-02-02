package org.wcong.ants.cluster;

import java.util.List;

/**
 * about node configure for local use
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/01/30
 */
public interface LocalNodeConfig {

	void load(String name);

	List<String> getSpiderPackages();

	void setTcpPort(int port);

	int getTcpPort();

	void setHttpPort(int port);

	int getHttpPort();

	String getNodeName();

	String getLocalIp();

}

package org.wcong.ants.cluster;

import java.util.List;

/**
 * about node configure
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/01/18
 */
public interface NodeConfig {

	void load(String name);

	List<String> getSpiderPackages();

	void setSpiderPackages(List<String> spiderPackages);

	void addSpiderPackage(String packageName);

	void setTcpPort(int port);

	int getTcpPort();

	void setHttpPort(int port);

	int getHttpPort();

	int setMaster(String ip, int port);

	String getMasterIp();

	int getMasterPort();

}

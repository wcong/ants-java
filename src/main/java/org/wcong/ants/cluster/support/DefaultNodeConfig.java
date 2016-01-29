package org.wcong.ants.cluster.support;

import org.wcong.ants.cluster.NodeConfig;

import java.util.Collections;
import java.util.List;

/**
 * a node  config
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/28
 */
public class DefaultNodeConfig implements NodeConfig {
	public List<String> getSpiderPackages() {
		return Collections.singletonList("org.wcong.ants.spiders");
	}

	public void setSpiderPackages(List<String> spiderPackages) {

	}

	public void addSpiderPackage(String packageName) {

	}
}

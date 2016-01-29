package org.wcong.ants;

import org.wcong.ants.cluster.Node;
import org.wcong.ants.cluster.NodeConfig;
import org.wcong.ants.cluster.support.DefaultNode;
import org.wcong.ants.cluster.support.DefaultNodeConfig;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) {
		Node node = new DefaultNode();
		NodeConfig nodeConfig = new DefaultNodeConfig();
		node.setNodeConfig(nodeConfig);
		node.init();
		node.start();
	}
}

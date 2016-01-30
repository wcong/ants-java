package org.wcong.ants;

import org.wcong.ants.cluster.Node;
import org.wcong.ants.cluster.NodeConfig;
import org.wcong.ants.cluster.support.DefaultNode;
import org.wcong.ants.cluster.support.DefaultNodeConfig;

/**
 * Hello world!
 */
public class Ants {

	public static void main(String[] args) {
		NodeConfig nodeConfig = new DefaultNodeConfig();
		nodeConfig.load("ants.properties");
		if (args.length > 1) {
			parseArgs(args, nodeConfig);
		}
		Node node = new DefaultNode();
		node.setNodeConfig(nodeConfig);
		node.init();
		node.start();
		node.stop();
	}

	private static void parseArgs(String[] args, NodeConfig nodeConfig) {
		int length = args.length;
		length /= 2;
		for (int i = 0; i < length; i++) {
			int index = i * 2;
			if (args[index].equals("-tcp")) {
				nodeConfig.setTcpPort(Integer.valueOf(args[index + 1]));
			} else if (args[index].equals("-http")) {
				nodeConfig.setHttpPort(Integer.valueOf(args[index + 1]));
			}
		}
	}
}

package org.wcong.ants;

import org.wcong.ants.cluster.Node;
import org.wcong.ants.cluster.NodeConfig;
import org.wcong.ants.cluster.support.DefaultNode;
import org.wcong.ants.cluster.support.DefaultNodeConfig;

import java.util.concurrent.CountDownLatch;

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
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final Node node = new DefaultNode();
        node.setNodeConfig(nodeConfig);
        node.init();
        node.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                node.stop();
                node.destroy();
            }
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            } else if (args[index].equals("-data")) {
                nodeConfig.setDataPath(args[index + 1]);
            }
        }
    }
}

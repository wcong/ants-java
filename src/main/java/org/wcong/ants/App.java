package org.wcong.ants;

import org.wcong.ants.rpc.Server;

/**
 * Hello world!
 */
public class App {
	public static void main(String[] args) {
		LifeCircle lifeCircle = new Server();
		lifeCircle.init();
		lifeCircle.start();
	}
}

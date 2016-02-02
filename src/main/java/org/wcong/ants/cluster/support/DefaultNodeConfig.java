package org.wcong.ants.cluster.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.cluster.NodeConfig;
import org.wcong.ants.util.NetUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * a node  config
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/28
 */
public class DefaultNodeConfig implements NodeConfig {

	private static final long serialVersionUID = -2662189168892524496L;

	private static Logger logger = LoggerFactory.getLogger(DefaultNodeConfig.class);

	private List<String> spiderPackages = Collections.singletonList("org.wcong.ants.spiders");

	private List<String> localhost = Arrays.asList("localhost", "127.0.0.1");

	private String localIp = NetUtils.getLocalIp();

	private String nodeName;

	private String masterIp;

	private int masterPort;

	private int tcpPort = 8200;

	private int httpPort = 8300;

	public void load(String name) {

		InputStream inputStream = DefaultNodeConfig.class.getClassLoader().getResourceAsStream("ants.properties");
		if (inputStream == null) {
			return;
		}
		Properties properties = new Properties();
		try {
			properties.load(inputStream);
		} catch (IOException e) {
			logger.error("load properties error", e);
		}
		String spiderPackages = properties.getProperty("spider.packages");
		if (spiderPackages != null) {
			this.spiderPackages = Arrays.asList(spiderPackages.split(","));
		}
		String tcpPort = properties.getProperty("tcp.port");
		if (tcpPort != null) {
			this.tcpPort = Integer.valueOf(tcpPort);
		}
		String httpPort = properties.getProperty("http.port");
		if (httpPort != null) {
			this.httpPort = Integer.valueOf(httpPort);
		}
		String masterName = properties.getProperty("master.node");
		if (masterName != null) {
			String[] masterInfo = masterName.split(":");
			if (masterInfo.length == 2) {
				int port = Integer.valueOf(masterInfo[1]);
				masterIp = masterInfo[0];
				if (localhost.contains(masterIp)) {
					masterIp = localIp;
				}
				masterPort = port;
			}
		}
	}

	public List<String> getSpiderPackages() {
		return spiderPackages;
	}

	public void setTcpPort(int port) {
		this.tcpPort = port;
	}

	public int getTcpPort() {
		return tcpPort;
	}

	public void setHttpPort(int port) {
		this.httpPort = port;
	}

	public int getHttpPort() {
		return httpPort;
	}

	public String getNodeName() {
		if (nodeName == null) {
			nodeName = localIp + ":" + tcpPort;
		}
		return nodeName;
	}

	public String getLocalIp() {
		return localIp;
	}

	public void setMaster(String ip, int port) {
		this.masterIp = ip;
		this.masterPort = port;
	}

	public String getMasterIp() {
		return masterIp;
	}

	public int getMasterPort() {
		return masterPort;
	}

	public boolean isLocalMaster() {
		return localIp.equals(masterIp) && tcpPort == masterPort;
	}
}

package org.wcong.ants.transport;

import lombok.Data;
import org.wcong.ants.cluster.NodeConfig;
import org.wcong.ants.spider.Request;
import org.wcong.ants.transport.message.CrawlResult;

import java.io.Serializable;
import java.util.Collection;

/**
 * message for transport
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/30
 */
@Data
public class TransportMessage implements Serializable {

	public static final int TYPE_REQUEST = 1;

	public static final int TYPE_CONFIG = 2;

	public static final int TYPE_RESULT = 3;

	private static final long serialVersionUID = 3696768612990766904L;

	private String nodeName;

	private int type;

	private Object object;

	public static TransportMessage newRequestMessage(String nodeName, Collection<? extends Request> requestList) {

		return new TransportMessage(nodeName, TYPE_REQUEST, requestList);
	}

	public static TransportMessage newRequestMessage(String nodeName, CrawlResult crawlResult) {
		return new TransportMessage(nodeName, TYPE_RESULT, crawlResult);
	}

	public static TransportMessage newRequestMessage(String nodeName, NodeConfig nodeConfig) {
		return new TransportMessage(nodeName, TYPE_CONFIG, nodeConfig);
	}

	public TransportMessage() {
	}

	private TransportMessage(String nodeName, int type, Object object) {
		this.nodeName = nodeName;
		this.type = type;
		this.object = object;
	}

}

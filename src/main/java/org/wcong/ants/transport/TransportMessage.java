package org.wcong.ants.transport;

import lombok.Data;
import org.wcong.ants.cluster.NodeConfig;
import org.wcong.ants.spider.Request;
import org.wcong.ants.transport.message.CrawlResult;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

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

    private List<? extends Request> requestList;

    private CrawlResult crawlResult;

    private NodeConfig nodeConfig;

    public static TransportMessage newRequestMessage(String nodeName, List<? extends Request> requestList) {
        TransportMessage message = new TransportMessage();
        message.setNodeName(nodeName);
        message.setType(TYPE_REQUEST);
        message.setRequestList(requestList);
        return message;
    }

    public static TransportMessage newRequestMessage(String nodeName, CrawlResult crawlResult) {
        TransportMessage message = new TransportMessage();
        message.setNodeName(nodeName);
        message.setType(TYPE_REQUEST);
        message.setCrawlResult(crawlResult);
        return message;
    }

    public static TransportMessage newRequestMessage(String nodeName, NodeConfig nodeConfig) {
        TransportMessage message = new TransportMessage();
        message.setNodeName(nodeName);
        message.setType(TYPE_REQUEST);
        message.setNodeConfig(nodeConfig);
        return message;
    }
}

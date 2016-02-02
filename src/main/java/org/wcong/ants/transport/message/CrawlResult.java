package org.wcong.ants.transport.message;

import lombok.Data;
import org.wcong.ants.spider.Request;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/2/1
 */
@Data
public class CrawlResult implements Serializable {

	private static final long serialVersionUID = -4522424916591907541L;

	private Request originRequest;

	private Collection<? extends Request> newRequests;

	public static CrawlResult newInstance(Request originRequest, Collection<? extends Request> newRequests) {
		CrawlResult crawlResult = new CrawlResult();
		crawlResult.setOriginRequest(originRequest);
		crawlResult.setNewRequests(newRequests);
		return crawlResult;
	}

}

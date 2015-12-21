package org.wcong.ants.downloader;

import lombok.Data;
import org.jsoup.nodes.Document;

/**
 * response
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/20
 */
@Data
public class Response {

	/**
	 * jsoup document
	 */
	private Document document;

	/**
	 * crawl depth
	 */
	private int depth;

	/**
	 * retry time
	 */
	private int retry;

	private String spiderName;

	private String parseName;

	private String nodeName;

}

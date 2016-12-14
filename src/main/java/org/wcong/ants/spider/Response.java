package org.wcong.ants.spider;

import lombok.Data;
import lombok.ToString;
import org.jsoup.nodes.Document;

import java.io.Serializable;

/**
 * response
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/20
 */
@Data()
public class Response implements Serializable {

	private static final long serialVersionUID = 5182729341404049314L;

	/**
	 * unique id
	 */
	private String id;

	/**
	 * response body
	 */
	private String body;

	/**
	 * source for response
	 */
	private Request request;

}

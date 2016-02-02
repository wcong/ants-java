package org.wcong.ants.crawler;

import lombok.Data;
import org.wcong.ants.spider.Request;

import java.util.List;

/**
 * crawler result
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/24
 */
@Data
public class Result {

	private List<Request> requestList;

}

package org.wcong.ants.crawler;

import org.wcong.ants.spider.Response;

/**
 * a parser
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/24
 */
public interface Parser {

	Result parse(Response response);

}

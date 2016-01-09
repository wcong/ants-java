/**
 * Copyright 2014-2015, NetEase, Inc. All Rights Reserved.
 * Date: 15/12/24
 */
package org.wcong.ants.crawler;

import org.wcong.ants.downloader.Response;

/**
 * a parser
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/24
 */
public interface Parser {

	Result parse(Response response);

}

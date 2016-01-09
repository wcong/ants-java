/**
 * Copyright 2014-2015, NetEase, Inc. All Rights Reserved.
 * Date: 15/12/24
 */
package org.wcong.ants.crawler;

/**
 * before and after a spider run
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/24
 */
public interface CrawlerIntegrate {

	void beafore();

	void after();

}

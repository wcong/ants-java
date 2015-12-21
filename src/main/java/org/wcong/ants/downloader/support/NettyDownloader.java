/**
 * Copyright 2014-2015, NetEase, Inc. All Rights Reserved.
 * Date: 15/12/18
 */
package org.wcong.ants.downloader.support;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.wcong.ants.downloader.Downloader;
import org.wcong.ants.downloader.Response;

/**
 * @author hzwangcong<hzwangcong@corp.netease.com>
 * @since 15/12/18
 */
public class NettyDownloader implements Downloader {

	public Response download() {
		Document document = Jsoup.parse("");
		return null;
	}
}

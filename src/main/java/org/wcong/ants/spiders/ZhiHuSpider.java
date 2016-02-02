/**
 * Copyright 2014-2015, NetEase, Inc. All Rights Reserved.
 * Date: 16/2/2
 */
package org.wcong.ants.spiders;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wcong.ants.crawler.Parser;
import org.wcong.ants.crawler.Result;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.Response;
import org.wcong.ants.spider.Spider;

import java.util.LinkedList;
import java.util.List;

/**
 * @author hzwangcong<hzwangcong@corp.netease.com>
 * @since 16/2/2
 */
public class ZhiHuSpider extends Spider {
	public ZhiHuSpider() {
		super("ZhiHu");
	}

	@Override
	public List<Request> getFirstRequests() {
		List<Request> requestList = new LinkedList<Request>();
		Request request = new Request();
		request.setUrl(
				"https://www.zhihu.com/node/ExploreAnswerListV2?params=%7B%22offset%22%3A10%2C%22type%22%3A%22day%22%7D");
		request.setSpiderName(name);
		request.setParseName(DEFAULT_PARSE_NAME);
		requestList.add(request);
		return requestList;
	}

	@Override
	public void init() {
		parserMap.put(DEFAULT_PARSE_NAME, new DefaultParser());
		parserMap.put("answer", new AnswerParse());
	}

	public static class DefaultParser implements Parser {

		public Result parse(Response response) {
			if (response == null) {
				return null;
			}
			Document document = response.getDocument();
			List<Request> requestList = new LinkedList<Request>();
			Elements aList = document.select(".explore-feed .question_link");
			for (Element a : aList) {
				System.out.println(a.text());
				Request request = new Request();
				requestList.add(request);
				request.setUrl("https://www.zhihu.com" + a.attr("href"));
				request.setSpiderName(response.getRequest().getSpiderName());
				request.setParseName("answer");

			}
			Result result = new Result();
			result.setRequestList(requestList);
			return result;
		}
	}

	public static class AnswerParse implements Parser {

		public Result parse(Response response) {
			if (response == null) {
				return null;
			}
			Document document = response.getDocument();
			Elements divList = document.select("#zh-question-detail .zm-editable-content");
			System.out.println(divList.text());
			return null;
		}
	}
}

package org.wcong.ants.spiders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
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
 * spider for dou ban
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/23
 */
public class DouBanSpider extends Spider {

	public DouBanSpider() {
		super("DouBan");
	}

	@Override
	public List<Request> getFirstRequests() {
		List<Request> requests = new LinkedList<Request>();
		Request request = new Request();
		request.setUrl("http://movie.douban.com/");
		request.setSpiderName(name);
		request.setParseName(DEFAULT_PARSE_NAME);
		requests.add(request);
		return requests;
	}

	/**
	 * init parseMap
	 */
	public void init() {
		parserMap.put(DEFAULT_PARSE_NAME, new DefaultParser());
	}

	public static class DefaultParser implements Parser {

		public Result parse(Response response) {
			if (response == null) {
				return null;
			}
			Document document = Jsoup.parse(response.getBody(),response.getRequest().getUrl());
			Elements liList = document.select(".screening-bd .ui-slide-content .ui-slide-item");
			for (Element li : liList) {
				Attributes attributes = li.attributes();
				for (Attribute attribute : attributes) {
					if (attribute.getKey().startsWith("data-")) {
						System.out.println(attribute.getValue());
					}
				}
			}
			return null;
		}
	}
}

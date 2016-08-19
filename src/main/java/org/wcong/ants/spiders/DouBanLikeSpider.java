package org.wcong.ants.spiders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wcong.ants.crawler.Parser;
import org.wcong.ants.crawler.Result;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.Response;
import org.wcong.ants.spider.Spider;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wcong on 2016/8/19.
 */
public class DouBanLikeSpider extends Spider {
    public DouBanLikeSpider() {
        super("DouBanLike");
    }

    public void init() {
        parserMap.put(DEFAULT_PARSE_NAME, new ListParser());
        parserMap.put("article", new DouBanGroupSpider.ArticleParser());
    }

    public List<Request> getFirstRequests() {
        List<Request> requests = new LinkedList<Request>();
        Request request = new Request();
        request.setUrl("https://www.douban.com/people/53117980/likes/");
        request.setSpiderName(name);
        request.setParseName(DEFAULT_PARSE_NAME);
        requests.add(request);
        return requests;
    }

    public static class ListParser implements Parser {

        public Result parse(Response response) {
            Document document = Jsoup.parse(response.getBody(), response.getRequest().getUrl());
            Elements article = document.select(".article");
            Elements aList = article.select(".fav-list li .title a");
            List<Request> requestList = new ArrayList<Request>(aList.size());
            for (Element h3 : aList) {
                String href = h3.attr("href");
                requestList.add(Request.basicRequest(href, response.getRequest().getSpiderName(), "article"));
            }
            Elements nextPage = article.select(".paginator .next a");
            if (nextPage != null) {
                String href = nextPage.attr("href");
                if (href != null && href.length() > 0) {
                    requestList.add(Request.basicRequest(href, response.getRequest().getSpiderName(), DEFAULT_PARSE_NAME));
                }
            }
            Result result = new Result();
            result.setRequestList(requestList);
            return result;
        }
    }

}

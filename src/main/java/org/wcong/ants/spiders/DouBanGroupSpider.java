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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * dou ban group article
 * Created by wcong on 2016/8/12.
 */
public class DouBanGroupSpider extends Spider {
    public DouBanGroupSpider() {
        super("DouBanGroup");
    }

    public void init() {
        parserMap.put(DEFAULT_PARSE_NAME, new ListParser());
        parserMap.put("article", new ArticleParser());
    }

    public List<Request> getFirstRequests() {
        List<Request> requests = new LinkedList<Request>();
        Request request = new Request();
        request.setUrl("https://www.douban.com/group/explore");
        request.setSpiderName(name);
        request.setParseName(DEFAULT_PARSE_NAME);
        requests.add(request);
        return requests;
    }

    public static class ListParser implements Parser {

        public Result parse(Response response) {
            Document document = Jsoup.parse(response.getBody(), response.getRequest().getUrl());
            Elements article = document.select(".article");
            Elements h3List = article.select(".channel-item div h3 a");
            List<Request> requestList = new ArrayList<Request>(h3List.size());
            for (Element h3 : h3List) {
                String href = h3.attr("href");
                requestList.add(Request.basicRequest(href, response.getRequest().getSpiderName(), "article"));
            }
            Elements nextPage = article.select(".paginator .next a");
            if (nextPage != null) {
                String href = nextPage.attr("href");
                if (href != null && href.length() > 0) {
                    requestList.add(Request.basicRequest("https://www.douban.com/group/explore" + href, response.getRequest().getSpiderName(), DEFAULT_PARSE_NAME));
                }
            }
            Result result = new Result();
            result.setRequestList(requestList);
            return result;
        }
    }

    public static class ArticleParser implements Parser {

        public Result parse(Response response) {
            Document document = Jsoup.parse(response.getBody(), response.getRequest().getUrl());
            String title = document.select("#content h1").text();
            String articleContent = document.select(".article").text();
            List<Map<String, Object>> articleList = new LinkedList<Map<String, Object>>();
            Map<String, Object> article = new HashMap<String, Object>();
            article.put("article", articleContent);
            article.put("title", title);
            article.put("path", response.getRequest().getUrl());
            articleList.add(article);
            Result result = new Result();
            List<Result.Data> dataList = new ArrayList<Result.Data>(1);
            result.setDataList(dataList);
            Result.Data data = new Result.Data();
            dataList.add(data);
            data.setIndex("article");
            data.setData(articleList);
            return result;
        }
    }
}

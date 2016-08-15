package org.wcong.ants.spiders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.wcong.ants.crawler.Parser;
import org.wcong.ants.crawler.Result;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.Response;
import org.wcong.ants.spider.Spider;
import org.wcong.ants.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * zhi hu recommend
 * Created by wcong on 2016/8/12.
 */
public class ZhiHuRecommend extends Spider {
    public ZhiHuRecommend() {
        super("ZhiHuRecommend");
    }

    public void init() {
        parserMap.put(DEFAULT_PARSE_NAME, new DefaultParser());
    }

    public List<Request> getFirstRequests() {
        List<Request> requestList = new ArrayList<Request>(1);
        Request request = new Request();
        requestList.add(request);
        request.setUrl("https://www.zhihu.com/node/ExploreRecommendListV2");
        request.setSpiderName(name);
        request.setParseName(DEFAULT_PARSE_NAME);
        request.setMethod(Request.METHOD_POST);
        request.setBody("method=next&params={\"offset\":0,\"limit\":20}");
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        request.setHeader(header);
        return requestList;
    }

    public static class DefaultParser implements Parser {

        public Result parse(Response response) {
            Map<String, Object> json = JsonUtils.parseJsonMap(response.getBody());
            List<String> contentList = (List<String>) json.get("msg");
            if (contentList == null) {
                return null;
            }
            Result result = new Result();
            List<Request> requestList = new LinkedList<Request>();
            result.setRequestList(requestList);
            requestList.add(makeNextRequest(response.getRequest()));
            List<Result.Data> dataList = new LinkedList<Result.Data>();
            result.setDataList(dataList);
            for (String content : contentList) {
                Result.Data data = new Result.Data();
                dataList.add(data);
                Document item = Jsoup.parse(content);
                String type = item.select(".zm-item").attr("data-type");
                if (type == null) {
                    continue;
                }
                data.setIndex(type);
                List<Map<String, Object>> dataContent = new LinkedList<Map<String, Object>>();
                data.setData(dataContent);
                Map<String, Object> soloData = new HashMap<String, Object>();
                dataContent.add(soloData);
                if ("Answer".equals(type)) {
                    Elements questionLinkElements = item.select("a.question_link");
                    String link = questionLinkElements.attr("href");
                    String title = questionLinkElements.text();
                    String contentText = item.select(".zm-item-rich-text textarea.content").text().replaceAll("<[^>]*>", "");
                    soloData.put("title", title);
                    soloData.put("link", link);
                    soloData.put("content", contentText);
                } else if ("Post".equals(type)) {
                    Elements postElements = item.select("a.post-link");
                    String title = postElements.text();
                    String link = postElements.attr("href");
                    String contentText = item.select(".zm-item-rich-text textarea.content").text().replaceAll("<[^>]*>", "");
                    soloData.put("title", title);
                    soloData.put("link", link);
                    soloData.put("content", contentText);
                }

            }
            return result;
        }

        private Request makeNextRequest(Request currentRequest) {
            Request nextRequest = new Request();
            nextRequest.setUrl(currentRequest.getUrl());
            nextRequest.setMethod(currentRequest.getMethod());
            nextRequest.setSpiderName(currentRequest.getSpiderName());
            nextRequest.setParseName(currentRequest.getParseName());
            int offset = 0;
            int limit = 20;
            String[] bodyArray = currentRequest.getBody().split("&");
            for (String soloBody : bodyArray) {
                String[] paramArray = soloBody.split("=");
                if ("params".equals(paramArray[0])) {
                    Map<String, Object> param = JsonUtils.parseJsonMap(paramArray[1]);
                    offset = (Integer) param.get("offset");
                    limit = (Integer) param.get("limit");
                    offset += limit;
                }
            }
            nextRequest.setBody("method=next&params={\"offset\":" + offset + ",\"limit\":" + limit + "}");
            nextRequest.setHeader(currentRequest.getHeader());
            return nextRequest;
        }
    }

}

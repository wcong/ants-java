package org.wcong.ants.spiders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.wcong.ants.crawler.Parser;
import org.wcong.ants.crawler.Result;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.Response;
import org.wcong.ants.spider.Spider;
import org.wcong.ants.util.JsonUtils;

import java.util.ArrayList;
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
        return requestList;
    }

    public static class DefaultParser implements Parser {

        public Result parse(Response response) {
            Document document = response.getDocument();
            Map<String, Object> json = JsonUtils.parseJsonMap(document.html());
            List<String> contentList = (List<String>) json.get("message");
            if (contentList == null) {
                return null;
            }
            for (String content : contentList) {
                Document answers = Jsoup.parse(content);
                String questionLink = answers.select(".question_link").attr("href");

            }
            return null;
        }
    }

}

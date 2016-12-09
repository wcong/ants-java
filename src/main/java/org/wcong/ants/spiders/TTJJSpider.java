package org.wcong.ants.spiders;

import org.apache.commons.dbcp2.BasicDataSource;
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
 * Created by wcong on 2016/12/8.
 */
public class TTJJSpider extends Spider {

    BasicDataSource basicDataSource = new BasicDataSource();

    {
        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("root123");
        basicDataSource.setUrl("jdbc:MySQL://localhost:3306/test?characterEncoding=utf8&amp;autoReconnect");
    }

    public TTJJSpider() {
        super("TTJJ");
    }

    public void init() {

    }

    public List<Request> getFirstRequests() {
        List<Request> requestList = new ArrayList<Request>();
        String url = "http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=all&rs=&gs=0&sc=zzf&st=desc&sd=2015-12-08&ed=2016-12-08&qdii=&tabSubtype=,,,,,&pi=1&pn=50&dx=1&v=0.559509082982047";
        requestList.add(Request.basicRequest(url, name, DEFAULT_PARSE_NAME));
        return requestList;
    }

    public static class DefaultParse implements Parser {

        public Result parse(Response response) {
            int first = response.getBody().indexOf("{");
            String json = response.getBody().substring(first, response.getBody().length() - 1);

            return null;
        }
    }
}

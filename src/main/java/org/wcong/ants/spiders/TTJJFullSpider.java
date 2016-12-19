package org.wcong.ants.spiders;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.crawler.Parser;
import org.wcong.ants.crawler.Result;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.Response;
import org.wcong.ants.spider.Spider;
import org.wcong.ants.util.JsonUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by wcong on 2016/12/13.
 */
public class TTJJFullSpider extends Spider {

    private static Logger logger = LoggerFactory.getLogger(TTJJFullSpider.class);

    private static String YIELD_URL = "http://fund.eastmoney.com/f10/F10DataApi.aspx?type=lsjz&code=%CODE&page=1&per=20&sdate=&edate=&rt=0.6751454283152365";

    public TTJJFullSpider() {
        super("TTJJFull");
    }

    public void init() {
        parserMap.put(DEFAULT_PARSE_NAME, new DefaultParse());
        parserMap.put("yield", new YieldParser());
        parserMap.put("yieldList", new YieldListParser());
    }

    public List<Request> getFirstRequests() {
        List<Request> requestList = new ArrayList<Request>();
        requestList.add(Request.basicRequest("http://fund.eastmoney.com/js/fundcode_search.js", getName(), DEFAULT_PARSE_NAME));
        return requestList;
    }

    public static class YieldListParser implements Parser {

        public Result parse(Response response) {
            String data = response.getBody();
            String url = response.getRequest().getUrl();
            YieldParser.parseYieldTable(url, data);
            return null;
        }
    }

    public static class YieldParser implements Parser {

        public Result parse(Response response) {
            String data = response.getBody();
            String url = response.getRequest().getUrl();
            parseYieldTable(url, data);
            int page = getPage(data);
            if (page > 1) {
                Result result = new Result();
                List<Request> requestList = new ArrayList<Request>(page - 1);
                result.setRequestList(requestList);
                String oldUrl = response.getRequest().getUrl();
                int oldUrlStart = oldUrl.indexOf("page");
                String prefix = oldUrl.substring(0, oldUrlStart);
                String suffix = oldUrl.substring(oldUrl.indexOf("&", oldUrlStart));
                String spiderName = response.getRequest().getSpiderName();
                for (int i = 2; i <= page; i++) {
                    requestList.add(Request.basicRequest(prefix + "page=" + i + suffix, spiderName, "yieldList"));
                }
                return result;
            }
            return null;
        }

        static void parseYieldTable(String url, String data) {
            int urlStart = url.indexOf("&code=") + "&code=".length();
            String code = url.substring(urlStart, url.indexOf("&", urlStart));
            int contentStart = data.indexOf("content:\"") + "content:\"".length();
            int contentEnd = data.indexOf("\"", contentStart);
            String content = data.substring(contentStart, contentEnd);
            Document document = Jsoup.parse(content);
            Elements elements = document.select("tbody tr");
            if( isMoney(document) ){
                for (Element element : elements) {
                    Elements tdList = element.select("td");
                    if( tdList.size() < 3 ){
                        logger.error("parse wrong"+tdList);
                        continue;
                    }
                    String date = tdList.get(0).text();
                    String benefit = tdList.get(1).text();
                    String yield7Days = tdList.get(2).text().replace("%", "");
                    String findYieldSql = "select * from tb_yield where code=\"" + code + "\" and date=\"" + date + "\"";
                    Connection connection = null;
                    try {
                        connection = TTJJSpider.basicDataSource.getConnection();
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(findYieldSql);
                        if (!resultSet.next()) {
                            String yieldSql = "insert into tb_money_yield(code,date,benefit,yield_7_days)" +
                                    "values(\"" + code + "\",\"" + date + "\"," + benefit + "," + yield7Days + ")";
                            statement.executeUpdate(yieldSql);
                        }
                    } catch (SQLException e) {
                        logger.error("something wrong", e);
                    } finally {
                        if (connection != null) {
                            try {
                                connection.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }else{
                for (Element element : elements) {
                    Elements tdList = element.select("td");
                    if( tdList.size() < 4 ){
                        logger.error("parse wrong"+tdList);
                        continue;
                    }
                    String date = tdList.get(0).text();
                    String value = tdList.get(1).text();
                    String sumValue = tdList.get(2).text();
                    String yield = tdList.get(3).text().replace("%", "");
                    String findYieldSql = "select * from tb_yield where code=\"" + code + "\" and date=\"" + date + "\"";
                    Connection connection = null;
                    try {
                        connection = TTJJSpider.basicDataSource.getConnection();
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(findYieldSql);
                        if (!resultSet.next()) {
                            if( yield.length() == 0 ){
                                yield = "0";
                            }
                            String yieldSql = "insert into tb_yield(code,date,value,sum_value,yield)" +
                                    "values(\"" + code + "\",\"" + date + "\"," + value + "," + sumValue + "," + yield + ")";
                            statement.executeUpdate(yieldSql);
                        }
                    } catch (SQLException e) {
                        logger.error("something wrong", e);
                    } finally {
                        if (connection != null) {
                            try {
                                connection.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        static boolean isMoney(Document document){
            return document.select("thead tr th").get(1).text().contains("每万份收益");
        }

        private int getPage(String data) {
            int start = data.indexOf("pages:") + "pages:".length();
            int end = data.indexOf(",", start);
            return Integer.parseInt(data.substring(start, end));
        }
    }

    public static class DefaultParse implements Parser {

        @SuppressWarnings("unchecked")
        public Result parse(Response response) {
            String data = response.getBody();
            int start = data.indexOf("[");
            int end = data.lastIndexOf(";");
            String jsonData = data.substring(start, end);
            List<Object> jijinList = JsonUtils.parseJsonList(jsonData);
            List<Request> requestList = new ArrayList<Request>(jijinList.size());
            String spiderName = response.getRequest().getSpiderName();
            for (Object object : jijinList) {
                List<Object> jijinData = (List<Object>) object;
                if (jijinData.size() < 4) {
                    logger.error("parse error" + jijinData);
                }
                String code = ((String) jijinData.get(0)).trim();
                requestList.add(Request.basicRequest(YIELD_URL.replace("%CODE", code), spiderName, "yield"));
                String abbreviate = ((String) jijinData.get(1)).trim();
                String name = ((String) jijinData.get(2)).trim();
                String type = ((String) jijinData.get(3)).trim();
                Connection connection = null;
                try {
                    String selectSql = "select * from tb_fundation where code = \"" + code + "\"";
                    connection = TTJJSpider.basicDataSource.getConnection();
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(selectSql);
                    if (!resultSet.next()) {
                        String insertSql = "insert into tb_fundation(code,name,abbreviate,type)values(\"" + code + "\",\"" + name + "\",\"" + abbreviate + "\",\"" + type + "\")";
                        statement.executeUpdate(insertSql);
                    }
                } catch (SQLException e) {
                    logger.error("something wrong", e);
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Result result = new Result();
            result.setRequestList(requestList);
            return result;
        }
    }
}

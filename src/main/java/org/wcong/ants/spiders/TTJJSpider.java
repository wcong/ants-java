package org.wcong.ants.spiders;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import org.apache.commons.dbcp2.BasicDataSource;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wcong on 2016/12/8.
 */
public class TTJJSpider extends Spider {

    private static Logger logger = LoggerFactory.getLogger(TTJJSpider.class);

    private static String URL_FORMAT = "http://fund.eastmoney.com/data/rankhandler.aspx?op=ph&dt=kf&ft=all&rs=&gs=0&sc=zzf&st=desc&sd=%DATE&ed=%DATE&qdii=&tabSubtype=,,,,,&pi=%PI&pn=50&dx=1&v=0.559509082982047";

    private static String CURRENT_URL_FORMAT = null;

    public static BasicDataSource basicDataSource = new BasicDataSource();

    static {
        basicDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("");
        basicDataSource.setUrl("jdbc:MySQL://localhost:3306/fundation?characterEncoding=utf8&amp;autoReconnect");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                try {
                    basicDataSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }));

    }


    public TTJJSpider() {
        super("TTJJ");
    }

    public void init() {
        parserMap.put(DEFAULT_PARSE_NAME, new DefaultParse());
        parserMap.put("datas", new DatasParse());
    }

    public List<Request> getFirstRequests() {
        List<Request> requestList = new ArrayList<Request>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(new Date());
        CURRENT_URL_FORMAT = URL_FORMAT.replace("%DATE", date);
        String url = CURRENT_URL_FORMAT.replace("%PI", "1");
        requestList.add(Request.basicRequest(url, name, DEFAULT_PARSE_NAME));
        return requestList;
    }

    public static class DatasParse implements Parser {

        public Result parse(Response response) {
            String data = response.getBody();
            DefaultParse.parseDatas(data);
            return null;
        }
    }

    public static class DefaultParse implements Parser {

        public Result parse(Response response) {
            String data = response.getBody();
            parseDatas(data);
            int allPageStart = data.indexOf("allPages:") + "allPages:".length();
            int allPageEnd = data.indexOf(",", allPageStart);
            int allPage = Integer.parseInt(data.substring(allPageStart, allPageEnd));
            Result result = new Result();
            List<Request> requestList = new ArrayList<Request>(allPage - 1);
            result.setRequestList(requestList);
            for (int i = 1; i < allPage; i++) {
                Request request = Request.basicRequest(CURRENT_URL_FORMAT.replace("%PI", String.valueOf(i)), response.getRequest().getSpiderName(), "datas");
                requestList.add(request);
            }
            return result;
        }

        public static void parseDatas(String data) {
            int start = data.indexOf("datas:[");
            int end = data.indexOf("]");
            String datas = data.substring(start + "datas:[".length() - 1, end + 1);
            List<Object> dataList = JsonUtils.parseJsonList(datas);
            for (Object soloData : dataList) {
                String jiJin = (String) soloData;
                String[] itemArray = jiJin.split(",");
                if (itemArray.length < 7) {
                    logger.error("parse error", soloData);
                }
                int code = Integer.parseInt(itemArray[0]);
                String name = itemArray[1];
                String abbreviate = itemArray[2];
                String date = itemArray[3];
                String yield = itemArray[6];
                String fondSql = "select * from tb_fundation where code=\"" + code+"\"";
                Connection connection = null;
                try {
                    connection = basicDataSource.getConnection();
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(fondSql);
                    if (!resultSet.next()) {
                        fondSql = "insert into tb_fundation(code,name,abbreviate)values(\"" + code + "\",\"" + name + "\",\"" + abbreviate + "\")";
                        statement.executeUpdate(fondSql);
                    }
                    String yieldSql = "select * from tb_yield where code=\"" + code + "\" and yield_date=\"" + yield + "\"";
                    resultSet = statement.executeQuery(yieldSql);
                    if (!resultSet.next() && yield.length()>0) {
                        yieldSql = "insert into tb_yield(code,yield_date,yield)values(\"" + code + "\",\"" + date + "\"," + yield + ")";
                        statement.executeUpdate(yieldSql);
                    }
                }catch(MySQLIntegrityConstraintViolationException exception){

                }catch (SQLException e) {
                    logger.error("something wrong",e);
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
}

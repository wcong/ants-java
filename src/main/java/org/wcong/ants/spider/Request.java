package org.wcong.ants.spider;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * about a  request
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/20
 */
@Data
public class Request implements Serializable {

    private static final long serialVersionUID = -3994932730423619546L;

    public static String METHOD_GET = "GET";

    public static String METHOD_POST = "POST";

    public static String METHOD_PUT = "PUT";

    public static String METHOD_DELETE = "DELETE";

    /**
     * a basic spider mainly for first request
     *
     * @param url        crawl url
     * @param spiderName spider of request
     * @param parseName  parser name for response
     * @return Request basic request
     */
    public static Request basicRequest(String url, String spiderName, String parseName) {
        Request request = new Request();
        request.setUrl(url);
        request.setSpiderName(spiderName);
        request.setParseName(parseName);
        return request;
    }

    private String id;

    private String url;

    private Map<String, String> header;

    private String body;

    private String method = METHOD_GET;

    private Map<String, String> cookies;

    private String name;

    private String spiderName;

    private String parseName;

    private String nodeName;

    private int depth;

    private String proxy;

    private int retry;

}

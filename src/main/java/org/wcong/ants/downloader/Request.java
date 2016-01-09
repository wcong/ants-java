package org.wcong.ants.downloader;

import lombok.Data;

import java.util.Map;

/**
 * about a  request
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/20
 */
@Data
public class Request {

	public static String METHOD_GET = "GET";

	public static String METHOD_POST = "POST";

	public static String METHOD_PUT = "PUT";

	public static String METHOD_DELETE = "DELETE";

	private String url;

	private Map<String, String> header;

	private String body;

	private String method;

	private int cookieJar;

	private String name;

	private String spiderName;

	private String parseName;

	private String nodeName;

	private int depth;

	private String proxy;

	private int retry;

}

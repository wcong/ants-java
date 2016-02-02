package org.wcong.ants.util;

import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.Response;

import java.util.Calendar;
import java.util.Random;

/**
 * generate request id
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/2/1
 */
public class IdGenerate {

	private static Random random = new Random();

	public static String generate(Request request) {
		return request.getNodeName() + Calendar.getInstance().getTimeInMillis() + random.nextInt();
	}

	public static String generate(Response response) {
		return response.getRequest().getNodeName() + Calendar.getInstance().getTimeInMillis() + random.nextInt();
	}

}

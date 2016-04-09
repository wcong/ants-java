package org.wcong.ants.crawler;

import lombok.Data;
import org.wcong.ants.spider.Request;

import java.util.List;
import java.util.Map;

/**
 * crawler result
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/24
 */
@Data
public class Result {

	private List<Request> requestList;

	private List<Data> dataList;

	@lombok.Data
	public static class Data {

		private String index;

		private List<Map<String, Object>> data;
	}

}

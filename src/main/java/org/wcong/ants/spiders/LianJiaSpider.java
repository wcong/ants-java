package org.wcong.ants.spiders;

import lombok.Data;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wcong.ants.crawler.Parser;
import org.wcong.ants.crawler.Result;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.Response;
import org.wcong.ants.spider.Spider;
import org.wcong.ants.util.JsonUtils;
import org.wcong.ants.util.RegexUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/8
 */
public class LianJiaSpider extends Spider {

	private static String domain = "http://hz.lianjia.com/";

	public LianJiaSpider() {
		super("LianJia");
	}

	@Override
	public void init() {
		parserMap.put(DEFAULT_PARSE_NAME, new DefaultParser());
	}

	@Override
	public List<Request> getFirstRequests() {
		List<Request> requestList = new LinkedList<Request>();
		requestList.add(Request.basicRequest("http://hz.lianjia.com/chengjiao/", name, DEFAULT_PARSE_NAME));
		return requestList;
	}

	public static class DefaultParser implements Parser {

		public Result parse(Response response) {
			if (response == null) {
				return null;
			}
			Document document = response.getDocument();
			Elements liList = document.select(".clinch-list li");
			List<HouseData> houseDataList = new ArrayList<HouseData>(liList.size());
			for (Element li : liList) {
				HouseData houseData = new HouseData();
				houseDataList.add(houseData);
				houseData.setImageUrl(li.select(".pic-panel a img").attr("src"));
				Elements infoPanel = li.select(".info-panel");
				houseData.setHouseTitle(infoPanel.select("h2 a").text());
				String otherText = infoPanel.select(".other .con").text();
				houseData.setOther(otherText);
				Elements detailType = infoPanel.select(".dealType .fl");
				houseData.setClosingTime(detailType.get(0).select(".div-cun").text());
				String unitPrice = detailType.get(1).select(".div-cun").text();
				Matcher unitPriceMatcher = RegexUtil.INT_NUMBER_PATTERN.matcher(unitPrice);
				if (unitPriceMatcher.find()) {
					houseData.setUnitPrice(Integer.parseInt(unitPriceMatcher.group()));
				}
				String totalPrice = infoPanel.select(".dealType .fr").select(".div-cun").first().text();
				Matcher totalPriceMatcher = RegexUtil.FLOAT_NUMBER_PATTERN.matcher(totalPrice);
				if (totalPriceMatcher.find()) {
					houseData.setTotalPrice(new BigDecimal(totalPriceMatcher.group()));
				}
			}
			System.out.println(houseDataList);
			String pageData = document.select(".page-box").attr("page-data");
			Map<String, Object> pageMap = JsonUtils.parseJsonMap(pageData);
			int totalPage = (Integer) pageMap.get("totalPage");
			int currentPage = (Integer) pageMap.get("curPage");
			Result result = new Result();
			if (currentPage < totalPage) {
				currentPage += 1;
				List<Request> requestList = new LinkedList<Request>();
				Request request = Request.basicRequest(domain + "/chengjiao/pg" + currentPage + "/",
						response.getRequest().getSpiderName(), Spider.DEFAULT_PARSE_NAME);
				requestList.add(request);
				result.setRequestList(requestList);
			}
			return result;
		}
	}

	@Data
	public static class HouseData {

		private String imageUrl;

		private String houseTitle;

		private String other;

		private String introduce;

		private String closingTime;

		private int unitPrice;

		private BigDecimal totalPrice;

	}
}

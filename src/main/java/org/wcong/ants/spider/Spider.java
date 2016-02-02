package org.wcong.ants.spider;

import lombok.Data;
import org.wcong.ants.crawler.CrawlerIntegrate;
import org.wcong.ants.crawler.Parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * about a spider
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/24
 */
@Data
public abstract class Spider {

	public static String DEFAULT_PARSE_NAME = "DEFAULT";

	protected List<CrawlerIntegrate> crawlerIntegrateList;

	protected final String name;

	protected Map<String, Object> extMap;

	public Spider(String name) {
		this.name = name;
	}

	protected Map<String, Parser> parserMap = new HashMap<String, Parser>();

	public Parser getParser(String parseName) {
		return parserMap.get(parseName);
	}

	public abstract void init();

	public abstract List<Request> getFirstRequests();

	public String getName() {
		return name;
	}

}

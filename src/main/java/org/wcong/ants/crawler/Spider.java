package org.wcong.ants.crawler;

import lombok.Data;

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

	private List<CrawlerIntegrate> crawlerIntegrateList;

	private List<String> initUrls;

	private String name;

	private Map<String, Object> extMap;

	private Map<String, Parser> parserMap;

}

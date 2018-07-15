package org.wcong.ants.spider;

import java.util.List;

/**
 * manage spider
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/29
 */
public interface SpiderManager {

    void loadSpider(List<String> spiderPackages);

    Spider getSpider(String name);

    List<String> getSpiderNames();

    void startSpider(String name);

}

package org.wcong.ants.util;

import org.junit.Test;
import org.wcong.ants.spider.Spider;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/25
 */
public class SpiderScannerTest {

	@Test
	public void testScan() {
		System.out.println(ClassScanner.scanPackages(Spider.class, "org.wcong.ants"));
	}

}

package org.wcong.ants.index.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/1
 */
public abstract class LuceneDocument {

	protected Analyzer analyzer = new StandardAnalyzer();

	private String rootPath;

	public static final String JOIN_SIGN = "_";

	public String makeIndexPath(String spider, String name) {
		return rootPath + "/" + spider + JOIN_SIGN + name;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

}

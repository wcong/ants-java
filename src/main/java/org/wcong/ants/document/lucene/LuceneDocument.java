package org.wcong.ants.document.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.lionsoul.jcseg.analyzer.v5x.JcsegAnalyzer5X;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;

import java.io.IOException;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/1
 */
public abstract class LuceneDocument {

	protected Analyzer analyzer =  new JcsegAnalyzer5X(JcsegTaskConfig.COMPLEX_MODE);

	private String rootPath;

	public static final String JOIN_SIGN = "_";

	public String makeIndexPath(String spider, String name) {
		return rootPath + "/" + spider + JOIN_SIGN + name;
	}

	public String[] splitPath(String path) {
		return path.split(JOIN_SIGN);
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getRootPath() {
		return rootPath;
	}

	public abstract IndexWriter getIndexWriter(String path) throws IOException;

}

/**
 * Copyright 2014-2015, NetEase, Inc. All Rights Reserved.
 * Date: 16/4/1
 */
package org.wcong.ants.index.lucene;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.index.DocumentReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hzwangcong<hzwangcong@corp.netease.com>
 * @since 16/4/1
 */
public class LuceneDocumentReader extends LuceneDocument implements DocumentReader {

	private static Logger logger = LoggerFactory.getLogger(LuceneDocumentReader.class);

	private LuceneDocumentWriter luceneDocumentWriter;

	private Map<String, IndexSearcher> searcherPool = new HashMap<String, IndexSearcher>(10);

	public List<Map<String, Object>> search(String spider, String index, String field, String value)
			throws IOException {
		String path = makeIndexPath(spider, index);
		IndexSearcher indexSearcher = getIndexSearcher(path);
		QueryParser parser = new QueryParser(field, analyzer);
		Query query;
		try {
			query = parser.parse(value);
		} catch (ParseException e) {
			logger.error("parse query error", e);
			return null;
		}
		ScoreDoc[] hits = indexSearcher.search(query, 1000).scoreDocs;
		return null;
	}

	public IndexSearcher getIndexSearcher(String path) throws IOException {
		IndexSearcher indexSearcher = searcherPool.get(path);
		if (indexSearcher != null) {
			return indexSearcher;
		} else {
			synchronized (this) {
				indexSearcher = searcherPool.get(path);
				if (indexSearcher != null) {
					return indexSearcher;
				} else {
					IndexWriter indexWriter = getIndexWriter(path);
					DirectoryReader iReader = DirectoryReader.open(indexWriter, false);
					indexSearcher = new IndexSearcher(iReader);
					searcherPool.put(path, indexSearcher);
					return indexSearcher;
				}
			}
		}

	}

	public void setLuceneDocumentWriter(LuceneDocumentWriter luceneDocumentWriter) {
		this.luceneDocumentWriter = luceneDocumentWriter;
	}

	@Override
	public IndexWriter getIndexWriter(String path) throws IOException {
		return luceneDocumentWriter.getIndexWriter(path);
	}
}

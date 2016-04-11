package org.wcong.ants.index.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.index.DocumentWriter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/1
 */
public class LuceneDocumentWriter extends LuceneDocument implements DocumentWriter {

	private Logger logger = LoggerFactory.getLogger(LuceneDocumentWriter.class);

	private Map<String, IndexWriter> indexWriterPool = new HashMap<String, IndexWriter>(10);

	public boolean writeDocument(String spider, String index, Map<String, Object> document) throws IOException {
		String path = makeIndexPath(spider, index);
		IndexWriter iWriter = getIndexWriter(path);
		Document doc = new Document();
		for (Map.Entry<String, Object> entry : document.entrySet()) {
			doc.add(new TextField(entry.getKey(), entry.getValue().toString(), Field.Store.YES));
		}
		iWriter.addDocument(doc);
		iWriter.commit();
		iWriter.maybeMerge();
		return true;
	}

	public boolean writeDocument(String spider, String index, List<Map<String, Object>> documentList)
			throws IOException {
		String path = makeIndexPath(spider, index);
		IndexWriter iWriter = getIndexWriter(path);
		List<Document> docList = new ArrayList<Document>(documentList.size());
		for (Map<String, Object> document : documentList) {
			Document doc = new Document();
			docList.add(doc);
			for (Map.Entry<String, Object> entry : document.entrySet()) {
				doc.add(new TextField(entry.getKey(), entry.getValue().toString(), Field.Store.YES));
			}
		}

		iWriter.addDocuments(docList);
		iWriter.commit();
		iWriter.maybeMerge();
		return true;
	}

	public IndexWriter getIndexWriter(String path) throws IOException {
		IndexWriter indexWriter = indexWriterPool.get(path);
		if (indexWriter != null) {
			return indexWriter;
		} else {
			synchronized (this) {
				indexWriter = indexWriterPool.get(path);
				if (indexWriter != null) {
					return indexWriter;
				} else {
					FSDirectory directory = FSDirectory.open(Paths.get(path));
					IndexWriterConfig config = new IndexWriterConfig(analyzer);
					indexWriter = new IndexWriter(directory, config);
					indexWriterPool.put(path, indexWriter);
					return indexWriter;
				}
			}
		}
	}

	public void init() {

	}

	public void start() {

	}

	public void pause() {

	}

	public void resume() {

	}

	public void stop() {

	}

	public void destroy() {
		for (Map.Entry<String,IndexWriter> entry : indexWriterPool.entrySet()) {
			try {
				logger.info("close index writer"+entry.getKey());
				entry.getValue().close();
				entry.getValue().getDirectory().close();
			} catch (IOException e) {
				logger.error("close index writer error", e);
			}
		}
	}
}

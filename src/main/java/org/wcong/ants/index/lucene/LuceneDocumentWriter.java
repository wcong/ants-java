package org.wcong.ants.index.lucene;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wcong.ants.index.DocumentWriter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/1
 */
public class LuceneDocumentWriter extends LuceneDocument implements DocumentWriter {

	private Map<String, Directory> directoryPool = new HashMap<String, Directory>(10);

	public boolean writeDocument(String spider, String name, Map<String, Object> document) throws IOException {
		String path = makeIndexPath(spider, name);
		Directory directory;
		if (directoryPool.containsKey(path)) {
			directory = directoryPool.get(path);
		} else {
			directory = FSDirectory.open(Paths.get(path));
		}
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter iwriter = new IndexWriter(directory, config);
		return false;
	}

	public boolean writeDocument(String spider, String name, List<Map<String, Object>> documents) {
		return false;
	}
}

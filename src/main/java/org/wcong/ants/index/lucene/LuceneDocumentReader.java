package org.wcong.ants.index.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.index.DocumentReader;
import org.wcong.ants.index.Documents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/1
 */
public class LuceneDocumentReader extends LuceneDocument implements DocumentReader {

    private static Logger logger = LoggerFactory.getLogger(LuceneDocumentReader.class);

    private LuceneDocumentWriter luceneDocumentWriter;

    private Map<String, IndexSearcher> searcherPool = new HashMap<String, IndexSearcher>(10);

    public Documents search(String spider, String index, String field, String value)
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
        TopDocs topDocs = indexSearcher.search(query, 1000);
        Documents documents = new Documents();
        documents.setTotal(topDocs.totalHits);
        documents.setPageNo(0);
        documents.setPageSize(1000);
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>(topDocs.scoreDocs.length);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Map<String, Object> data = new HashMap<String, Object>();
            dataList.add(data);
            Document document = indexSearcher.doc(scoreDoc.doc);
            for (IndexableField indexableField : document.getFields()) {
                data.put(indexableField.name(), indexableField.stringValue());
            }
        }
        documents.setDataList(dataList);
        return documents;
    }

    private IndexSearcher getIndexSearcher(String path) throws IOException {
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
        for (Map.Entry<String,IndexSearcher> entry : searcherPool.entrySet()) {
            try {
                logger.info("close index searcher"+entry.getKey());
                entry.getValue().getIndexReader().close();
            } catch (IOException e) {
                logger.error("close index writer error", e);
            }
        }
    }
}

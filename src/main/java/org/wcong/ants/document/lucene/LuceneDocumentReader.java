package org.wcong.ants.document.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.document.DocumentReader;
import org.wcong.ants.document.DocumentTerms;
import org.wcong.ants.document.Documents;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/1
 */
public class LuceneDocumentReader extends LuceneDocument implements DocumentReader {

	private static Logger logger = LoggerFactory.getLogger(LuceneDocumentReader.class);

	private LuceneDocumentWriter luceneDocumentWriter;

	private final Map<String, IndexSearcher> searcherPool = new HashMap<String, IndexSearcher>(10);

	private final Map<String,IndexReader> readerPool = new HashMap<String, IndexReader>(10);

	public Documents search(String spider, String index, String field, String value) throws IOException {
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
		documents.setSearchField(field);
		documents.setSearchQuery(value);
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

    public DocumentTerms sumTerms(String spider, String index) throws IOException {
		return sumTermsForField(spider, index, null);
    }

	private DocumentTerms sumTermsForField(String spider, String index, String sumField) throws IOException {
		IndexReader indexReader = getIndexReader(makeIndexPath(spider, index));
		DocumentTerms documentTerms = new DocumentTerms();
		List<DocumentTerms.DocumentTerm> documentTermList = new LinkedList<DocumentTerms.DocumentTerm>();
		documentTerms.setTerms(documentTermList);
		Set<String> termSet = new HashSet<String>();
		Fields fields =  MultiFields.getFields(indexReader);
		for( String field : fields ){
			if( sumField != null && !sumField.equals(field) ){
				continue;
			}
            Terms terms = fields.terms(field);
            TermsEnum termsEnum = terms.iterator();
            BytesRef bytesRef;
            while ((bytesRef=termsEnum.next()) != null){
                DocumentTerms.DocumentTerm documentTerm = new DocumentTerms.DocumentTerm();
                String termName = bytesRef.utf8ToString();
                if( termSet.contains(termName) ){
                    continue;
                }
                termSet.add(termName);
                documentTermList.add(documentTerm);
                documentTerm.setTerm(termName);
                documentTerm.setDocCount(termsEnum.docFreq());
                documentTerm.setTermCount(termsEnum.totalTermFreq());
	            documentTerm.setWeight(documentTerm.getTermCount()*indexReader.numDocs()/(documentTerm.getDocCount()*documentTerm.getDocCount()));
            }
        }
		Collections.sort(documentTermList, new Comparator<DocumentTerms.DocumentTerm>() {
            public int compare(DocumentTerms.DocumentTerm o1, DocumentTerms.DocumentTerm o2) {
                return (int)(o2.getWeight() - o1.getWeight());
            }
        });
		return documentTerms;
	}

	public DocumentTerms sumTerms(String spider, String index, String field) throws IOException {
		return sumTermsForField(spider, index, field);
	}

	public Map<String, List<String>> readDocuments() {
		String rootPath = getRootPath();
		File rootFile = new File(rootPath);
		File[] files = rootFile.listFiles();
		if (files == null) {
			return Collections.emptyMap();
		}
		Map<String, List<String>> dataMap = new HashMap<String, List<String>>(files.length);
		for (File file : files) {
			String name = file.getName();
			String[] path = splitPath(name);
			String spider = path[0];
			String index = path[1];
			List<String> indexList = dataMap.get(spider);
			if (indexList == null) {
				indexList = new LinkedList<String>();
				dataMap.put(spider, indexList);
			}
			indexList.add(index);
		}
		return dataMap;
	}

	private IndexSearcher getIndexSearcher(String path) throws IOException {
		IndexSearcher indexSearcher = searcherPool.get(path);
		if (indexSearcher != null) {
			return indexSearcher;
		} else {
			synchronized (searcherPool) {
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

	private IndexReader getIndexReader(String path) throws IOException{
		IndexReader indexReader = readerPool.get(path);
        if( indexReader != null ){
            return indexReader;
        }
        synchronized (readerPool){
            indexReader = readerPool.get(path);
            if( indexReader != null ){
                return indexReader;
            }else{
                IndexWriter indexWriter = getIndexWriter(path);
                DirectoryReader iReader = DirectoryReader.open(indexWriter, false);
                readerPool.put(path, iReader);
                return iReader;
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
		for (Map.Entry<String, IndexSearcher> entry : searcherPool.entrySet()) {
			try {
				logger.info("close index searcher" + entry.getKey());
				entry.getValue().getIndexReader().close();
			} catch (IOException e) {
				logger.error("close index writer error", e);
			}
		}
	}
}

package org.wcong.ants.document;

import org.wcong.ants.LifeCircle;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * read index
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/1
 */
public interface DocumentReader extends LifeCircle, DocumentPath {

	Documents search(String spider, String index, String field, String value) throws IOException;

	DocumentTerms sumTerms(String spider, String index)throws IOException;

	DocumentTerms sumTerms(String spider, String index, String field)throws IOException;

	Map<String, List<String>> readDocuments();

}

package org.wcong.ants.document;

import org.wcong.ants.LifeCircle;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * write index
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/1
 */
public interface DocumentWriter extends LifeCircle, DocumentPath {

	boolean writeDocument(String spider, String index, Map<String, Object> document) throws IOException;

	boolean writeDocument(String spider, String index, List<Map<String, Object>> documents) throws IOException;

}

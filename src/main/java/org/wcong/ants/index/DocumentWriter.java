package org.wcong.ants.index;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * write index
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/1
 */
public interface DocumentWriter {

	boolean writeDocument(String spider, String name, Map<String, Object> document) throws IOException;

	boolean writeDocument(String spider, String name, List<Map<String, Object>> documents);

}

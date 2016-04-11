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
public interface DocumentReader extends DocumentPath {

	List<Map<String, Object>> search(String spider, String index, String field, String value) throws IOException;

}

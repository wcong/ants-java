package org.wcong.ants.document;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/11
 */
@Data
public class Documents {

	private long total;

	private int pageNo;

	private int pageSize;

	private String searchField;

	private String searchQuery;

	private List<Map<String, Object>> dataList;

}

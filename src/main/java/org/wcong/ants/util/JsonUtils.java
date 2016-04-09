package org.wcong.ants.util;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/9
 */
public class JsonUtils {

	private static ObjectMapper objectMapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseJsonMap(String jsonString) {
		try {
			return objectMapper.readValue(jsonString, Map.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyMap();
	}

}

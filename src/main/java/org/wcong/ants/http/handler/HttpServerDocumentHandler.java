package org.wcong.ants.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.aware.DocumentReaderAware;
import org.wcong.ants.http.HttpServerHandler;
import org.wcong.ants.index.DocumentReader;
import org.wcong.ants.index.Documents;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * search document
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/11
 */
public class HttpServerDocumentHandler extends HttpServerHandler implements DocumentReaderAware {

	private Logger logger = LoggerFactory.getLogger(HttpServerDocumentHandler.class);

	private DocumentReader documentReader;

	public void setDocumentReader(DocumentReader documentReader) {
		this.documentReader = documentReader;
	}

	@Override
	public void handleRequest(ChannelHandlerContext ctx, HttpRequest request, QueryStringDecoder query,
			HttpContent content) {
		releaseContent(content);
		byte[] data = null;
		try {
			SearchParam searchParam = makeSearchParam(query);
			if (searchParam.valid()) {
				Documents documents = documentReader
						.search(searchParam.getSpider(), searchParam.getIndex(), searchParam.getField(),
								searchParam.getQuery());
				data = objectMapper.writeValueAsBytes(documents);
			}
		} catch (IOException e) {
			logger.error("json encode error", e);
		}
		if (data == null) {
			data = new byte[0];
		}
		sendResponse(ctx, request, data);
	}

	private SearchParam makeSearchParam(QueryStringDecoder query) {
		SearchParam searchParam = new SearchParam();
		Map<String, List<String>> parameterMap = query.parameters();
		List<String> spider = parameterMap.get("spider");
		if (spider != null && !spider.isEmpty()) {
			searchParam.setSpider(spider.get(0));
		}
		List<String> index = parameterMap.get("index");
		if (index != null && !index.isEmpty()) {
			searchParam.setIndex(index.get(0));
		}
		List<String> field = parameterMap.get("field");
		if (field != null && !field.isEmpty()) {
			searchParam.setField(field.get(0));
		}
		List<String> searchQuery = parameterMap.get("query");
		if (searchQuery != null && !searchQuery.isEmpty()) {
			searchParam.setQuery(searchQuery.get(0));
		}
		return searchParam;

	}

	@Override
	public String getHandlerUri() {
		return "/document";
	}

	@Data
	public static class SearchParam {

		private String spider;

		private String index;

		private String field;

		private String query;

		public boolean valid() {
			return spider != null && index != null && field != null && query != null;
		}
	}
}

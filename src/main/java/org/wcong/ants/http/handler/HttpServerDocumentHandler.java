package org.wcong.ants.http.handler;

import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.document.DocumentReader;
import org.wcong.ants.document.Documents;
import org.wcong.ants.http.HttpServerHandler;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * search document
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/11
 */
public class HttpServerDocumentHandler extends HttpServerHandler {

    private Logger logger = LoggerFactory.getLogger(HttpServerDocumentHandler.class);

    private DocumentReader documentReader;

    public HttpServerDocumentHandler(DocumentReader documentReader) {
        this.documentReader = documentReader;
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
    public NettyOutbound handleRequest(HttpServerRequest request, HttpServerResponse response) {
        SearchParam searchParam = makeSearchParam(new QueryStringDecoder(request.uri()));
        if (!searchParam.valid()) {
            return sendResponse(response, "param wrong");
        }
        Documents documents;
        try {
            documents = documentReader
                    .search(searchParam.getSpider(), searchParam.getIndex(), searchParam.getField(),
                            searchParam.getQuery());
        } catch (IOException e) {
            logger.error("search wrong" + e.getMessage(), e);
            return sendResponse(response, e.getMessage());
        }
        return sendResponse(response, documents);
    }

    @Override
    public boolean test(HttpServerRequest request) {
        return request.uri().equals("/document");
    }

    @Data
    public static class SearchParam {

        private String spider;

        private String index;

        private String field;

        private String query;

        boolean valid() {
            return spider != null && index != null && field != null && query != null;
        }
    }
}

package org.wcong.ants.http.handler;

import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.document.DocumentReader;
import org.wcong.ants.document.DocumentTerms;
import org.wcong.ants.http.HttpServerHandler;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * analysis for document
 * Created by wcong on 2016/8/12.
 */
public class HttpServerDocumentAnalysisHandler extends HttpServerHandler {
    private Logger logger = LoggerFactory.getLogger(HttpServerDocumentAnalysisHandler.class);

    private DocumentReader documentReader;

    public HttpServerDocumentAnalysisHandler(DocumentReader documentReader) {
        this.documentReader = documentReader;
    }

    @Override
    public NettyOutbound handleRequest(HttpServerRequest request, HttpServerResponse response) {
        String spider = null;
        String index = null;
        String field = null;
        int start = 0;
        int limit = 500;
        Map<String, List<String>> parameterMap = new QueryStringDecoder(request.uri()).parameters();
        List<String> spiderList = parameterMap.get("spider");
        if (spiderList != null && !spiderList.isEmpty()) {
            spider = spiderList.get(0);
        }
        List<String> indexList = parameterMap.get("index");
        if (indexList != null && !indexList.isEmpty()) {
            index = indexList.get(0);
        }
        List<String> fieldList = parameterMap.get("field");
        if (fieldList != null && !fieldList.isEmpty()) {
            field = fieldList.get(0);
        }
        List<String> startList = parameterMap.get("start");
        if (startList != null && !startList.isEmpty()) {
            start = Integer.parseInt(startList.get(0));
        }
        List<String> limitList = parameterMap.get("limit");
        if (limitList != null && !limitList.isEmpty()) {
            limit = Integer.parseInt(limitList.get(0));
        }
        if (spider != null && index != null) {
            DocumentTerms documentTerms;
            try {
                documentTerms = documentReader.sumTerms(spider, index, field);
            } catch (IOException e) {
                logger.error("sumTerms error", e);
                return sendResponse(response, e.getMessage());
            }
            if (start >= documentTerms.getTerms().size()) {
                documentTerms.setTerms(Collections.<DocumentTerms.DocumentTerm>emptyList());
            } else {
                int end = start + limit;
                if (end > documentTerms.getTerms().size()) {
                    end = documentTerms.getTerms().size();
                }
                documentTerms.setTerms(documentTerms.getTerms().subList(start, end));
            }
            return sendResponse(response, documentTerms);
        }
        return sendResponse(response, "");
    }

    @Override
    public boolean test(HttpServerRequest request) {
        return request.uri().startsWith("/document/analysis");
    }
}

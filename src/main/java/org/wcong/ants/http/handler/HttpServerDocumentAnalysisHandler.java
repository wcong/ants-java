package org.wcong.ants.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.aware.DocumentReaderAware;
import org.wcong.ants.document.DocumentReader;
import org.wcong.ants.document.DocumentTerms;
import org.wcong.ants.http.HttpServerHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * analysis for document
 * Created by wcong on 2016/8/12.
 */
public class HttpServerDocumentAnalysisHandler extends HttpServerHandler implements DocumentReaderAware {
    private Logger logger = LoggerFactory.getLogger(HttpServerDocumentAnalysisHandler.class);

    private DocumentReader documentReader;

    public void setDocumentReader(DocumentReader documentReader) {
        this.documentReader = documentReader;
    }

    public void handleRequest(ChannelHandlerContext ctx, HttpRequest request, QueryStringDecoder query, HttpContent content) {
        releaseContent(content);
        byte[] data = null;
        try {
            String spider = null;
            String index = null;
            Map<String, List<String>> parameterMap = query.parameters();
            List<String> spiderList = parameterMap.get("spider");
            if (spiderList != null && !spiderList.isEmpty()) {
                spider = spiderList.get(0);
            }
            List<String> indexList = parameterMap.get("index");
            if (indexList != null && !indexList.isEmpty()) {
                index = indexList.get(0);
            }
            if (spider != null && index != null) {
                DocumentTerms documentTerms = documentReader.sumTerms(spider, index);
                data = objectMapper.writeValueAsBytes(documentTerms);
            }
        } catch (IOException e) {
            logger.error("json encode error", e);
        }
        if (data == null) {
            data = new byte[0];
        }
        sendResponse(ctx, request, data);
    }

    public String getHandlerUri() {
        return "/document/analysis";
    }
}

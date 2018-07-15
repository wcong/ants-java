package org.wcong.ants.http.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.document.DocumentReader;
import org.wcong.ants.http.HttpServerHandler;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.util.List;
import java.util.Map;

/**
 * list all document
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/11
 */
public class HttpServerDocumentsHandler extends HttpServerHandler {

    private static Logger logger = LoggerFactory.getLogger(HttpServerDocumentsHandler.class);

    private DocumentReader documentReader;

    public HttpServerDocumentsHandler(DocumentReader documentReader) {
        this.documentReader = documentReader;
    }

    @Override
    public NettyOutbound handleRequest(HttpServerRequest request, HttpServerResponse response) {
        Map<String, List<String>> documents = documentReader.readDocuments();
        return sendResponse(response, documents);
    }

    @Override
    public boolean test(HttpServerRequest request) {
        return request.uri().equals("/documents");
    }
}

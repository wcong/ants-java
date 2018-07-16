package org.wcong.ants.http;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.cluster.Node;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import java.io.IOException;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

/**
 * about node
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/01/28
 */
public abstract class HttpServerHandler {

    private static Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    protected Node node;

    public abstract NettyOutbound handleRequest(HttpServerRequest request, HttpServerResponse response);

    public abstract boolean test(HttpServerRequest request);

    protected NettyOutbound sendResponse(HttpServerResponse response, Object data) {
        if (logger.isDebugEnabled()) {
            logger.debug("send response" + response.toString() + data.toString());
        }
        byte[] bytes = null;
        try {
            bytes = objectMapper.writeValueAsBytes(data);
        } catch (IOException e) {
            logger.error("encode error", e);
        }

        bytes = bytes == null ? new byte[0] : bytes;
        response.header(CONTENT_TYPE, "application/json; charset=UTF-8");
        response.header(CONTENT_LENGTH, String.valueOf(bytes.length));
        return response.sendByteArray(Mono.just(bytes));
    }
}

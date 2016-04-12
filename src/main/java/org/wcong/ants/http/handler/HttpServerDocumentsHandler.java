package org.wcong.ants.http.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.aware.DocumentReaderAware;
import org.wcong.ants.http.HttpServerHandler;
import org.wcong.ants.index.DocumentReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * list all document
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/11
 */
public class HttpServerDocumentsHandler extends HttpServerHandler implements DocumentReaderAware {

	private static Logger logger = LoggerFactory.getLogger(HttpServerDocumentsHandler.class);

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
			Map<String, List<String>> documents = documentReader.readDocuments();
			data = objectMapper.writeValueAsBytes(documents);
		} catch (IOException e) {
			logger.error("json encode error", e);
		}
		if (data == null) {
			data = new byte[0];
		}
		sendResponse(ctx, request, data);
	}

	@Override
	public String getHandlerUri() {
		return "/documents";
	}
}

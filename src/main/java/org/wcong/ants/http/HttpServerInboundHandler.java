package org.wcong.ants.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.aware.DocumentReaderAware;
import org.wcong.ants.aware.NodeAware;
import org.wcong.ants.cluster.Node;
import org.wcong.ants.document.DocumentReader;
import org.wcong.ants.util.ClassScanner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/24
 */
@ChannelHandler.Sharable
public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter implements NodeAware, DocumentReaderAware {

	private static Logger logger = LoggerFactory.getLogger(HttpServerInboundHandler.class);

	private static Set<String> STATIC_FILE_PREFIX = new HashSet<String>(Arrays.asList("html","js","css","jpg"));

    private HttpStaticFileServerHandler httpStaticFileServerHandler = new HttpStaticFileServerHandler();

	private Node node;

	private DocumentReader documentReader;

	private Map<String, HttpServerHandler> handlerMap = new HashMap<String, HttpServerHandler>();

	{
		List<Class<HttpServerHandler>> handlerClassList = ClassScanner
				.scanPackage(HttpServerHandler.class, "org.wcong.ants.http.handler");
		for (Class<HttpServerHandler> handlerClass : handlerClassList) {
			HttpServerHandler handler = null;
			try {
				handler = handlerClass.newInstance();
			} catch (Exception e) {
				logger.error("setQueue handler error", e);
			}
			if (handler != null) {
				handlerMap.put(handler.getHandlerUri(), handler);
			}
		}
	}

	public void setNode(Node node) {
		this.node = node;
		for (HttpServerHandler httpServerHandler : handlerMap.values()) {
			httpServerHandler.setNode(node);
		}
	}

	private HttpRequest request;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpRequest) {
			request = (HttpRequest) msg;

			String uri = request.getUri();
			logger.info("Uri:" + uri);
		}
		if (msg instanceof HttpContent) {
            if( isStaticFile(request.getUri()) ){
                httpStaticFileServerHandler.channelRead(ctx,request);
                return;
            }
			QueryStringDecoder query = new QueryStringDecoder(request.getUri());
			HttpServerHandler handler = handlerMap.get(query.path());
			HttpContent httpContent = (HttpContent) msg;
			if (handler != null) {
				handler.handleRequest(ctx, request, query, httpContent);
			} else {
				handlerMap.get("").handleRequest(ctx, request, query, httpContent);
			}
		}
	}

    private boolean isStaticFile(String uri){
        String[] urlArray = uri.split("\\?");
        int lastIndex = urlArray[0].lastIndexOf(".");
        String fileType = urlArray[0].substring(lastIndex+1);
        return STATIC_FILE_PREFIX.contains(fileType);
    }

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("error caught", cause);
		ctx.close();
	}

	public void setDocumentReader(DocumentReader documentReader) {
		this.documentReader = documentReader;
		for (HttpServerHandler handler : handlerMap.values()) {
			if (handler instanceof DocumentReaderAware) {
				((DocumentReaderAware) handler).setDocumentReader(documentReader);
			}
		}
	}
}

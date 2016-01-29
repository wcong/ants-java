package org.wcong.ants.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.LifeCircle;
import org.wcong.ants.cluster.Node;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/24
 */
public class HttpServer implements LifeCircle {

	private static Logger logger = LoggerFactory.getLogger(HttpServer.class);

	private Node node;

	private int port = 8300;

	private EventLoopGroup bossGroup = new NioEventLoopGroup();

	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	private ChannelFuture f;

	public void setPort(int port) {
		this.port = port;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public void init() {

	}

	public void start() {

		ServerBootstrap b = new ServerBootstrap();
		final HttpServerInboundHandler httpServerInboundHandler = new HttpServerInboundHandler();
		httpServerInboundHandler.setNode(node);
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						// server端发送的是httpResponse，所以要使用HttpResponseEncoder进行编码
						ch.pipeline().addLast(new HttpResponseEncoder());
						// server端接收到的是httpRequest，所以要使用HttpRequestDecoder进行解码
						ch.pipeline().addLast(new HttpRequestDecoder());
						ch.pipeline().addLast(httpServerInboundHandler);
					}
				}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
		try {
			f = b.bind(port).sync();
		} catch (Exception e) {
			logger.error("get error", e);
		}
	}

	public void pause() {

	}

	public void resume() {

	}

	public void stop() {
		try {
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			logger.error("close error", e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public void destroy() {

	}

}

package org.wcong.ants.transport;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.LifeCircle;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/25
 */
public class TransportClient implements LifeCircle {

	private static Logger logger = LoggerFactory.getLogger(TransportClient.class);

	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	private ChannelFuture f;

	private ClientHandler.ClientInHandler clientInHandler;

	private String ip;

	private int port;

	public TransportClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public void run() {
		Bootstrap b = new Bootstrap();
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new MessageDecoder(), new MessageEncoder(), new ClientHandler.ClientOutHandler(),
						clientInHandler);
			}
		});
		try {
			f = b.connect(ip, port).sync();
			logger.info("connect to {}:{}", ip, port);
		} catch (InterruptedException e) {
			logger.error("client error", e);
		}

	}

	public Channel getChannel() {
		return f.channel();
	}

	public void init() {

	}

	public void start() {
		run();
	}

	public void pause() {

	}

	public void resume() {

	}

	public void stop() {
		try {
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			logger.error("client close error", e);
		} finally {
			workerGroup.shutdownGracefully();
		}
	}

	public void destroy() {

	}

	public void setClientInHandler(ClientHandler.ClientInHandler clientInHandler) {
		this.clientInHandler = clientInHandler;
	}
}

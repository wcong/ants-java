package org.wcong.ants.rpc;

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
public class Client implements LifeCircle {

	private static Logger logger = LoggerFactory.getLogger(Client.class);

	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	private ChannelFuture f;

	public void run() {
		Bootstrap b = new Bootstrap();
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new MessageDecoder(), new MessageEncoder(), new ClientHandler.ClientOutHandler(),
						new ClientHandler.ClientInHandler());
			}
		});
		try {
			f = b.connect("localhost", 8200).sync();
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
}

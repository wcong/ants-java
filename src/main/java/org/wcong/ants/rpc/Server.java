package org.wcong.ants.rpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.LifeCircle;

/**
 * a rpc server
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/18
 */
public class Server implements LifeCircle {

	private static Logger logger = LoggerFactory.getLogger(Server.class);

	private int port = 8200;

	private EventLoopGroup bossGroup = new NioEventLoopGroup();

	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	public static void addChannel(Channel channel) {
		channelGroup.add(channel);
	}

	public static void removeChannel(Channel channel) {
		channelGroup.remove(channel);
	}

	public static Channel getChannel(String name) {
		return channelGroup.iterator().next();
	}

	private ChannelFuture f;

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
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
			logger.info("close server");
		} catch (InterruptedException e) {
			logger.error("shutdown error", e);
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

	public void destroy() {

	}

	public void run() {
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup);
			b.channel(NioServerSocketChannel.class);
			b.option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			b.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
							.addLast(new MessageEncoder(), new MessageDecoder(), new ServerHandler.ServerInHandler(),
									new ServerHandler.ServerOutHandler());
				}
			});
			f = b.bind(port).sync();
			logger.info("start listen");
		} catch (Exception e) {
			logger.error("server error", e);
		}
	}
}

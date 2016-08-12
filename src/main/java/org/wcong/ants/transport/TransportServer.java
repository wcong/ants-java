package org.wcong.ants.transport;

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

import java.util.HashMap;
import java.util.Map;

/**
 * a rpc server
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/18
 */
public class TransportServer implements LifeCircle {

	private static Logger logger = LoggerFactory.getLogger(TransportServer.class);

	private int port = 8200;

	private EventLoopGroup bossGroup = new NioEventLoopGroup();

	private EventLoopGroup workerGroup = new NioEventLoopGroup();

	private ServerHandler.ServerInHandler inHandler;

	private static Map<String,ChannelHandlerContext> ctxMap = new HashMap<String, ChannelHandlerContext>();

	private static Map<String,ChannelPromise> promiseMap = new HashMap<String, ChannelPromise>();

	private static Map<String,Object> dataMap = new HashMap<String, Object>();

	public static void addChannel(String name,ChannelHandlerContext channel) {
		ctxMap.put(name, channel);
	}

	public static ChannelPromise channelPromise(String name){
		return promiseMap.get(name);
	}

	public static void removeChannel(String name) {
		ctxMap.remove(name);
	}

	public static Channel getChannel(String name) {
		return ctxMap.get(name).channel();
	}

	public static <T> T getSyncResponse(String name,String message,T t) throws InterruptedException {
		ChannelPromise channelPromise = promiseMap.get(name);
		if( channelPromise != null ){
			channelPromise.await();
		}
		Channel channel = ctxMap.get(name).channel();
		channelPromise = channel.writeAndFlush(message).channel().newPromise();
		promiseMap.put(name,channelPromise);
		channelPromise.await();
		return (T) dataMap.get(name);
	}

	private ChannelFuture f;

	public void setPort(int port) {
		this.port = port;
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
					ch.pipeline().addLast(new MessageEncoder(), new MessageDecoder(), inHandler,
							new ServerHandler.ServerOutHandler());
				}
			});
			f = b.bind(port).sync();
			logger.info("start listen at {}", port);
		} catch (Exception e) {
			logger.error("server error", e);
		}
	}

	public void setServerInHandler(ServerHandler.ServerInHandler inHandler) {
		this.inHandler = inHandler;

	}
}

package org.wcong.ants.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/25
 */
public class Client {

	private static Logger logger = LoggerFactory.getLogger(Client.class);

	public void run() {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(workerGroup);
		b.channel(NioSocketChannel.class);
		b.option(ChannelOption.SO_KEEPALIVE, true);
		b.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(new MessageEncoder(), new ClientHandler.ClientOutHandler(),
						new ClientHandler.ClientInHandler());
			}
		});
		try {
			ChannelFuture f = b.connect("localhost", 8200).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			logger.error("client error", e);
		} finally {
			workerGroup.shutdownGracefully();
		}

	}

}

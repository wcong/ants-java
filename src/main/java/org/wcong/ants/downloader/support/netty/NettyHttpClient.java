package org.wcong.ants.downloader.support.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http client of netty
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/26
 */
public class NettyHttpClient {

	private static Logger logger = LoggerFactory.getLogger(NettyHttpClient.class);

	public static NettyHttpClient getInstance(String host) {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap(); // (1)
			b.group(workerGroup); // (2)
			b.channel(NioSocketChannel.class); // (3)
			b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new HttpResponseDecoder());
					ch.pipeline().addLast(new HttpRequestEncoder());
				}
			});

			// Start the client.
			ChannelFuture f = b.connect(host, 80).sync(); // (5)

			// Wait until the connection is closed.
			f.channel().closeFuture().sync();

		} catch (Exception e) {
			logger.error("init NettyHttpClient for host:" + host + " error", e);
		} finally {
			workerGroup.shutdownGracefully();
		}
		return null;
	}

}

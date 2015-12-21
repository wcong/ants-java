package org.wcong.ants.rpc;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.wcong.ants.LifeCircle;

import java.net.InetSocketAddress;

/**
 * a rpc server
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/18
 */
public class Server implements LifeCircle {

	private ServerBootstrap serverBootstrap;

	public void init() {
		ChannelFactory channelFactory = new NioServerSocketChannelFactory();
		serverBootstrap = new ServerBootstrap(channelFactory);
		serverBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				return Channels.pipeline(new ServerHandler());
			}
		});
		serverBootstrap.setOption("child.tcpNoDelay", true);
		serverBootstrap.setOption("child.keepAlive", true);
	}

	public void start() {
		serverBootstrap.bind(new InetSocketAddress(8080));
	}

	public void pause() {
	}

	public void resume() {

	}

	public void stop() {
		serverBootstrap.shutdown();
	}

	public void destroy() {

	}
}

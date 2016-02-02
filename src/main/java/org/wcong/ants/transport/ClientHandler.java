package org.wcong.ants.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.aware.ClusterAware;
import org.wcong.ants.aware.QueueAware;
import org.wcong.ants.cluster.Cluster;
import org.wcong.ants.cluster.NodeConfig;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.RequestBlockingQueue;
import org.wcong.ants.spider.ResponseBlockingQueue;

import java.util.Collection;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/25
 */
public class ClientHandler {

	public static class ClientInHandler extends ChannelInboundHandlerAdapter implements QueueAware, ClusterAware {

		private static Logger logger = LoggerFactory.getLogger(ClientInHandler.class);

		private RequestBlockingQueue requests;

		private Cluster cluster;

		@Override
		@SuppressWarnings("unchecked")
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			logger.info("get meg " + msg);
			if (msg instanceof TransportMessage) {
				TransportMessage transportMessage = (TransportMessage) msg;
				switch (transportMessage.getType()) {
				case TransportMessage.TYPE_REQUEST:
					requests.addWaiting((Collection<? extends Request>) transportMessage.getObject());
					break;
				case TransportMessage.TYPE_CONFIG:
					cluster.addNodeConfig((NodeConfig) transportMessage.getObject());
					break;
				}
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			logger.error("error", cause);
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			ctx.writeAndFlush(
					TransportMessage.newRequestMessage(cluster.getLocalNode().getNodeName(), cluster.getLocalNode()));
		}

		public void setQueue(RequestBlockingQueue requests, ResponseBlockingQueue responses) {
			this.requests = requests;
		}

		public void setCluster(Cluster cluster) {
			this.cluster = cluster;
		}
	}

	public static class ClientOutHandler extends ChannelOutboundHandlerAdapter {

		private static Logger logger = LoggerFactory.getLogger(ClientOutHandler.class);

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
			ctx.write(msg, promise);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			logger.error("error", cause);
		}
	}
}

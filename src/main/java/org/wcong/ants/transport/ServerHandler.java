package org.wcong.ants.transport;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.aware.ClusterAware;
import org.wcong.ants.aware.ClusterQueueAware;
import org.wcong.ants.cluster.Cluster;
import org.wcong.ants.cluster.ClusterRequestBlockingQueue;
import org.wcong.ants.cluster.NodeConfig;
import org.wcong.ants.transport.message.CrawlResult;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/25
 */
public class ServerHandler {

	public static class ServerInHandler extends ChannelInboundHandlerAdapter
			implements ClusterQueueAware, ClusterAware {

		private static Logger logger = LoggerFactory.getLogger(ServerInHandler.class);

		private Cluster cluster;

		private ClusterRequestBlockingQueue clusterRequestQueue;

		@Override
		@SuppressWarnings("unchecked")
		public void channelRead(ChannelHandlerContext ctx, Object msg) {
			logger.info("get message " + msg);
			if (msg instanceof TransportMessage) {
				TransportMessage transportMessage = (TransportMessage) msg;
				switch (transportMessage.getType()) {
				case TransportMessage.TYPE_RESULT:
					CrawlResult crawlResult = (CrawlResult) transportMessage.getObject();
					clusterRequestQueue.addToCluster(crawlResult.getOriginRequest(), crawlResult.getNewRequests());
					break;
				case TransportMessage.TYPE_CONFIG:
					cluster.addNodeConfig((NodeConfig) transportMessage.getObject());
					break;
				}
			} else {
				logger.error("wrong type {}", msg);
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			logger.error("get error", cause);
		}

		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			TransportServer.addChannel(ctx.channel());
		}

		public void setCluster(Cluster cluster) {
			this.cluster = cluster;
		}

		public void setClusterQueue(ClusterRequestBlockingQueue clusterRequestBlockingQueue) {
			this.clusterRequestQueue = clusterRequestBlockingQueue;
		}
	}

	public static class ServerOutHandler extends ChannelOutboundHandlerAdapter {

		private static Logger logger = LoggerFactory.getLogger(ServerOutHandler.class);

		@Override
		public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
			ctx.write(msg, promise);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			logger.error("get error", cause);
		}
	}

}

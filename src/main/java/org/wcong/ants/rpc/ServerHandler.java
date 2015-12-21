package org.wcong.ants.rpc;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/18
 */
public class ServerHandler extends SimpleChannelHandler {

	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		ChannelBuffer channelBuffer = ChannelBuffers.buffer(100);
		for (char c : "end".toCharArray()) {
			channelBuffer.writeByte(c);
		}
		e.getChannel().write(channelBuffer);
		e.getChannel().close();
	}

	/**
	 * Invoked when an exception was raised by an I/O thread or a
	 * {@link ChannelHandler}.
	 */
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}
}

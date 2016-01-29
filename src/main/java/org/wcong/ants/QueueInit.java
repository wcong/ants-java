package org.wcong.ants;

import org.wcong.ants.downloader.Request;
import org.wcong.ants.downloader.Response;

import java.util.concurrent.BlockingQueue;

/**
 * init queue
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/29
 */
public interface QueueInit {

	void setQueue(BlockingQueue<Request> requests, BlockingQueue<Response> responses);

}

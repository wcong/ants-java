package org.wcong.ants.downloader.support.netty;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.wcong.ants.Status;
import org.wcong.ants.downloader.Downloader;
import org.wcong.ants.downloader.Request;
import org.wcong.ants.downloader.Response;

import java.util.concurrent.BlockingQueue;

/**
 * downloader of netty
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/18
 */
public class NettyDownloader implements Downloader {

	private BlockingQueue<Request> requests;

	private BlockingQueue<Response> responses;

	private Status status = Status.NONE;

	public void init(BlockingQueue<Request> requests, BlockingQueue<Response> responses) {
		this.requests = requests;
		this.responses = responses;
	}

	public void start() {
		status = Status.RUNNING;
	}

	public void parse() {
		status = Status.PURSE;
	}

	public void resume() {
		status = Status.RUNNING;
	}

	public Response download() {
		Document document = Jsoup.parse("");
		return null;
	}
}

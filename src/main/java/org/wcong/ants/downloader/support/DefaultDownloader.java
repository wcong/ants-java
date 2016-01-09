package org.wcong.ants.downloader.support;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.Status;
import org.wcong.ants.downloader.Downloader;
import org.wcong.ants.downloader.Request;
import org.wcong.ants.downloader.Response;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * default downloader for ants
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/26
 */
public class DefaultDownloader implements Downloader {

	private static Logger logger = LoggerFactory.getLogger(DefaultDownloader.class);

	private BlockingQueue<Request> requests;

	private BlockingQueue<Response> responses;

	private ExecutorService downloadThreadPool = Executors.newFixedThreadPool(16);

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

	public Response download(Request request) {

	}

	private HttpRequest makeRequest(Request request){
		HttpRequest httpRequest =
	}

	public void run() {
		if (requests == null) {
			return;
		}
		Request request;
		try {
			while ((request = requests.take()) != null) {
				Response response = download(request);
				if (response != null) {
					responses.add(response);
				}
			}
		} catch (Exception e) {
			logger.error("run downloader error", e);
			status = Status.PURSE;
		}
	}

}

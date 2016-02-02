package org.wcong.ants.downloader.support;

import org.apache.http.HttpEntity;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.ants.Status;
import org.wcong.ants.downloader.Downloader;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.RequestBlockingQueue;
import org.wcong.ants.spider.Response;
import org.wcong.ants.spider.ResponseBlockingQueue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * default downloader for ants
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/26
 */
public class DefaultDownloader implements Downloader {

	private static Logger logger = LoggerFactory.getLogger(DefaultDownloader.class);

	private CloseableHttpClient httpClient = HttpClients.createDefault();

	private RequestBlockingQueue requests;

	private ResponseBlockingQueue responses;

	private ExecutorService downloadThreadPool = Executors.newFixedThreadPool(16);

	private volatile Status status = Status.NONE;

	public void setQueue(RequestBlockingQueue requests, ResponseBlockingQueue responses) {
		this.requests = requests;
		this.responses = responses;
	}

	public void init() {

	}

	public void start() {
		status = Status.RUNNING;
		run();
	}

	public void pause() {

	}

	public void parse() {
		status = Status.PURSE;
	}

	public void resume() {
		status = Status.RUNNING;
	}

	public void stop() {
		status = Status.STOP;
	}

	public void destroy() {

	}

	public void download(final Request request) {
		logger.info("start download request {}", request);
		downloadThreadPool.submit(new Runnable() {
			public void run() {
				try {
					HttpUriRequest httpUriRequest = makeRequest(request);
					CloseableHttpResponse httpResponse = httpClient.execute(httpUriRequest);
					logger.info("downloaded request {}", request);
					Response response = makeResponse(request, httpResponse);
					if (response != null) {
						responses.addWaiting(Collections.singleton(response));
						logger.info("push response to queue");
					}
				} catch (Exception e) {
					logger.error("download error", e);
				}
			}
		});
	}

	private Response makeResponse(Request request, CloseableHttpResponse httpResponse) throws IOException {
		HttpEntity entity = httpResponse.getEntity();
		String content = EntityUtils.toString(entity);
		httpResponse.close();
		Response response = new Response();
		response.setDocument(Jsoup.parse(content, request.getUrl()));
		response.setRequest(request);
		return response;
	}

	private HttpUriRequest makeRequest(Request request)
			throws MethodNotSupportedException, UnsupportedEncodingException {
		RequestBuilder requestBuilder = RequestBuilder.create(request.getMethod()).setUri(request.getUrl());
		if (request.getBody() != null) {
			requestBuilder.setEntity(new StringEntity(request.getBody()));
		}
		if (request.getHeader() != null) {
			for (Map.Entry<String, String> header : request.getHeader().entrySet()) {
				requestBuilder.addHeader(header.getKey(), header.getValue());
			}
		}
		return requestBuilder.build();
	}

	public void run() {
		if (requests == null) {
			return;
		}
		status = Status.RUNNING;
		Request request;
		try {
			while (status == Status.RUNNING) {
				if ((request = requests.poll(1, TimeUnit.SECONDS)) != null) {
					download(request);
				}
			}
		} catch (Exception e) {
			logger.error("run downloader error", e);
			status = Status.PURSE;
		}
		downloadThreadPool.shutdown();
	}

}

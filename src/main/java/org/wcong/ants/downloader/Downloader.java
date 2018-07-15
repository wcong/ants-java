package org.wcong.ants.downloader;

import org.wcong.ants.LifeCircle;
import org.wcong.ants.aware.QueueAware;
import org.wcong.ants.spider.Request;
import org.wcong.ants.spider.Response;
import reactor.netty.http.client.HttpClientResponse;

/**
 * download html
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/18
 */
public interface Downloader extends LifeCircle, QueueAware {

    void download(Request request) throws Exception;

    default Response makeResponse(Request request, HttpClientResponse httpResponse, String content) {
        Response response = new Response();
        response.setBody(content);
        response.setRequest(request);
        return response;
    }

}

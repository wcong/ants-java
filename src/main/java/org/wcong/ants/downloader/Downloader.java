package org.wcong.ants.downloader;

/**
 * download html
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/18
 */
public interface Downloader {

	Response download(Request request);

}

package org.wcong.ants.downloader;

import org.wcong.ants.LifeCircle;
import org.wcong.ants.QueueInit;

/**
 * download html
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/18
 */
public interface Downloader extends LifeCircle, QueueInit {

	void download(Request request) throws Exception;

}

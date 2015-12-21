package org.wcong.ants;

/**
 * life of a component
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 15/12/18
 */
public interface LifeCircle {

	void init();

	void start();

	void pause();

	void resume();

	void stop();

	void destroy();

}

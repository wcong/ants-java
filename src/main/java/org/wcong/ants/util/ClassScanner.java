package org.wcong.ants.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/24
 */
public class ClassScanner {

	private static Logger logger = LoggerFactory.getLogger(ClassScanner.class);

	private static final char DIR_SEPARATOR = '/';

	private static final char PKG_SEPARATOR = '.';

	private static final String CLASS_FILE_SUFFIX = ".class";

	/**
	 * scan package for given class
	 *
	 * @param spiderType spider  type class
	 * @param packages   packages
	 * @return class list
	 */
	public static <T> List<Class<T>> scanPackages(Class<T> spiderType, String... packages) {
		List<Class<T>> classList = new LinkedList<Class<T>>();
		for (String basicPackage : packages) {
			classList.addAll(scanPackage(spiderType, basicPackage));
		}
		return classList;
	}

	/**
	 * scan package for given class
	 *
	 * @param spiderType  spider  type class
	 * @param packageName packageName
	 * @return class list
	 */
	public static <T> List<Class<T>> scanPackage(Class<T> spiderType, String packageName) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> urls = null;
		try {
			urls = classLoader.getResources(packageName.replace(PKG_SEPARATOR, DIR_SEPARATOR));
		} catch (IOException e) {
			logger.error("get urls error", e);
		}
		if (urls == null) {
			return Collections.emptyList();
		}
		List<Class<T>> classList = new LinkedList<Class<T>>();
		while (urls.hasMoreElements()) {
			classList.addAll(scanUrl(spiderType, urls.nextElement(), packageName));
		}
		return classList;
	}

	/**
	 * scan a url
	 *
	 * @param spiderType  spider  type class
	 * @param url         url
	 * @param packageName basic package name
	 * @return class list
	 */
	public static <T> List<Class<T>> scanUrl(Class<T> spiderType, URL url, String packageName) {
		if (url == null || spiderType == null || packageName == null) {
			return Collections.emptyList();
		}
		File packageDir = new File(url.getFile());
		File[] childFiles = packageDir.listFiles();
		if (childFiles == null) {
			return Collections.emptyList();
		}
		List<Class<T>> classList = new LinkedList<Class<T>>();
		for (File file : childFiles) {
			classList.addAll(scanFile(spiderType, file, packageName));
		}
		return classList;
	}

	/**
	 * scan a file
	 *
	 * @param spiderType  spider  type class
	 * @param file        file
	 * @param packageName name of package
	 * @return class list
	 */
	public static <T> List<Class<T>> scanFile(Class<T> spiderType, File file, String packageName) {
		List<Class<T>> classList = new LinkedList<Class<T>>();
		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles != null) {
				for (File childFile : childFiles) {
					classList.addAll(scanFile(spiderType, childFile, packageName + PKG_SEPARATOR + file.getName()));
				}
			}
		} else if (file.getName().endsWith(CLASS_FILE_SUFFIX)) {
			String resource = packageName + PKG_SEPARATOR + file.getName();
			int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
			String className = resource.substring(0, endIndex);
			try {
				Class detectedClass = Class.forName(className);
				if (spiderType.isAssignableFrom(detectedClass)) {
					classList.add(detectedClass);
				}
			} catch (ClassNotFoundException ignore) {
				logger.error("class not found", ignore);
			}
		}
		return classList;
	}

}

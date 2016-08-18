package org.wcong.ants.util;

import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/1/24
 */
public class ClassScanner {

    private static Logger logger = LoggerFactory.getLogger(ClassScanner.class);

    /**
     * scan package for given class
     *
     * @param classType type class
     * @param packages  packages
     * @return class list
     */
    public static <T> List<Class<? extends T>> scanPackages(Class<T> classType, String... packages) {
        List<URL> urls = new LinkedList<URL>();
        for (String basicPackage : packages) {
            urls.addAll(ClasspathHelper.forPackage(basicPackage));
        }
        Configuration configuration = new ConfigurationBuilder().addUrls(urls);
        Reflections reflections = new Reflections(configuration);
        Set<Class<? extends T>> classSet = reflections.getSubTypesOf(classType);
        if (classSet == null) {
            return Collections.emptyList();
        }
        List<Class<? extends T>> classList = new ArrayList<Class<? extends T>>(classSet.size());
        for (Class<? extends T> findClass : classSet) {
            classList.add(findClass);
        }
        return classList;
    }

    /**
     * scan package for given class
     *
     * @param classType   type class
     * @param packageName packageName
     * @return class list
     */
    public static <T> List<Class<? extends T>> scanPackage(Class<T> classType, String packageName) {
        return scanPackages(classType, packageName);
    }

}

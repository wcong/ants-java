package org.wcong.ants.util;

import java.util.regex.Pattern;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/4/9
 */
public class RegexUtil {

	public static Pattern INT_NUMBER_PATTERN = Pattern.compile("[0-9]+");

	public static Pattern FLOAT_NUMBER_PATTERN = Pattern.compile("[0-9]+[\\.]*[0-9]");

}

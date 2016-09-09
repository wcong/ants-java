package org.wcong.ants.util;

import java.util.List;

/**
 * Created by wcong on 2016/9/9.
 */
public class ListUtil {

    public static String join(List<String> list, String joint) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        StringBuilder sb = new StringBuilder();
        for (String solo : list) {
            if (sb.length() > 0) {
                sb.append(joint);
            }
            sb.append(solo);
        }
        return sb.toString();
    }
}

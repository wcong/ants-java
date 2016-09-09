package org.wcong.ants.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by wcong on 2016/9/9.
 */
public class ListUtilTest {

    @Test
    public void testJoin() {
        Assert.assertTrue(ListUtil.join(null, ",").equals(""));
        Assert.assertTrue(ListUtil.join(Arrays.asList("a", "b"), ",").equals("a,b"));
    }

}

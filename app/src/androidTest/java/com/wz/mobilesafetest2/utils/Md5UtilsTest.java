package com.wz.mobilesafetest2.utils;

import junit.framework.TestCase;

/**
 * Created by Administrator on 2015/10/1 0001.
 */
public class Md5UtilsTest extends TestCase {

    public void testMd5() throws Exception {
        String s = Md5Utils.md5("123456");
        System.out.println(s);
    }
}
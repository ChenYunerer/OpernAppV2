package com.yun.opernv2.utils;

import com.orhanobut.logger.Logger;

/**
 * Created by Yun on 2015/10/27.
 * 自定义log，show控制是否打印log
 */
public class LogUtil {
    private static final boolean show = true;

    public static void v(String tag, String msg) {
        if (show) Logger.v(tag + ": %s", msg);
    }

    public static void i(String tag, String msg) {
        if (show) Logger.i(tag + ": %s", msg);
    }

    public static void d(String tag, String msg) {
        if (show) Logger.d(tag + ": %s", msg);
    }

    public static void w(String tag, String msg) {
        if (show) Logger.w(tag + ": %s", msg);
    }

    public static void e(String tag, String msg) {
        if (show) Logger.e(tag + ": %s", msg);
    }
}

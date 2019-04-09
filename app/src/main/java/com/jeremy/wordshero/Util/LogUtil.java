package com.jeremy.wordshero.Util;

import android.util.Log;

/**
 * 日志工具类
 * 在项目中打印日志都可以调用此类
 *
 * @author cyd
 * @since 2018/12/1
 */
public class LogUtil {
    public static final int VERBOSE = 1;

    public static final int DEBUG = 2;

    public static final int INFO = 3;

    public static final int WARN = 4;

    public static final int ERROR = 5;

    public static final int NOTHING = 6;

    public static final int level = VERBOSE;

    public static void v(String tag,String msg) {
        if (level <= VERBOSE) {
            Log.v(tag,msg);
        }
    }

    public static void d(String tag,String msg) {
        if (level <= DEBUG) {
            Log.d(tag,msg);
        }
    }

    public static void i(String tag,String msg) {
        if (level <= INFO) {
            Log.i(tag,msg);
        }
    }

    public static void w(String tag,String msg) {
        if (level <= WARN) {
            Log.w(tag,msg);
        }
    }

    public static void e(String tag,String msg) {
        if (level <= ERROR) {
            Log.e(tag,msg);
        }
    }



}

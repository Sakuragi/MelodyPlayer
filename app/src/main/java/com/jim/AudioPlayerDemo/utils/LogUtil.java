package com.jim.AudioPlayerDemo.utils;

import android.util.Log;

import com.jim.AudioPlayerDemo.BuildConfig;


/**
 * LogUtil
 * Created by Jim on 2017/11/2 0002.
 */
@SuppressWarnings("unused")
public class LogUtil {

    private static String className;//类名
    private static String methodName;//方法名
    private static int lineNumber;//行数

    private static String lastMethodName;

    private static boolean debug = BuildConfig.DEBUG;
    public static String TAG = "hzs";

    private LogUtil() {
    }


    private static String printLog(String log) {
        return "| " + Thread.currentThread().getName() + " | " +
                lastMethodName+"() -->" + methodName + "() | " +
                " (" + className + ":" + lineNumber + ") | " +
                log;
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
        lastMethodName=sElements[2].getMethodName();
    }


    public static void e(String message) {
        if (debug) {
            getMethodNames(new Throwable().getStackTrace());
            Log.e(TAG, printLog(message));
        }

    }

    public static void e(String tag, String message) {
        if (debug) {
            getMethodNames(new Throwable().getStackTrace());
            Log.e(TAG + "_" + tag, printLog(message));
        }

    }


    public static void i(String message) {
        if (debug) {
            getMethodNames(new Throwable().getStackTrace());
            Log.i(TAG, printLog(message));
        }
    }

    public static void i(String tag, String message) {
        if (debug) {
            getMethodNames(new Throwable().getStackTrace());
            Log.i(TAG + "_" + tag, printLog(message));
        }
    }

    public static void d(String message) {
        if (debug) {
            getMethodNames(new Throwable().getStackTrace());
            Log.d(TAG, printLog(message));
        }
    }

    public static void d(String tag, String message) {
        if (debug) {
            getMethodNames(new Throwable().getStackTrace());
            Log.d(TAG + "_" + tag, printLog(message));
        }
    }

    public static void v(String message) {
        if (debug) {
            getMethodNames(new Throwable().getStackTrace());
            Log.v(TAG, printLog(message));
        }
    }

    public static void v(String tag, String message) {
        if (debug) {
            getMethodNames(new Throwable().getStackTrace());
            Log.v(TAG + "_" + tag, printLog(message));
        }
    }

    public static void w(String message) {
        if (debug) {
            getMethodNames(new Throwable().getStackTrace());
            Log.w(TAG, printLog(message));
        }
    }

    public static void w(String tag, String message) {
        if (debug) {
            getMethodNames(new Throwable().getStackTrace());
            Log.w(TAG + "_" + tag, printLog(message));
        }
    }

    public static void wtf(String message) {
        if (debug) {
            getMethodNames(new Throwable().getStackTrace());
            Log.wtf(TAG, printLog(message));
        }
    }


}

package com.myAllVideoBrowser.util;

import android.util.Log;
import com.myAllVideoBrowser.BuildConfig;
import com.myAllVideoBrowser.DLApplication;

public class AppLogger {

    private static final String TAG = DLApplication.DEBUG_TAG;

    public static void d(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void i(String message) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, message);
        }
    }

    public static void w(String message) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, message);
        }
    }

    public static void e(String message) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message);
        }
    }
}

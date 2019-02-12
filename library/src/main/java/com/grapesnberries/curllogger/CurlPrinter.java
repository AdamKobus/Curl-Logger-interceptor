package com.grapesnberries.curllogger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by mohamedzakaria on 6/7/16.
 */
class CurlPrinter {
    /**
     * Drawing toolbox
     */
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";

    private static final String DEFAULT_TAG = "CURL";

    static void print(@Nullable final String tag, String url, String msg) {
        StringBuilder logMsg = new StringBuilder("\n");
        logMsg.append("\n");
        logMsg.append("URL: ").append(url);
        logMsg.append("\n");
        logMsg.append(SINGLE_DIVIDER);
        logMsg.append("\n");
        logMsg.append(msg);
        logMsg.append(" ");
        logMsg.append(" \n");
        logMsg.append(SINGLE_DIVIDER);
        logMsg.append(" \n ");
        Log.d(obtainNonNullTag(tag), logMsg.toString());
    }

    static void printError(@Nullable String tag, @NonNull Throwable t) {
        Log.e(obtainNonNullTag(tag),
              "Error when processing request",
              t);
    }

    private static String obtainNonNullTag(@Nullable String tag) {
        if (tag != null) {
            return tag;
        }
        return DEFAULT_TAG;
    }
}

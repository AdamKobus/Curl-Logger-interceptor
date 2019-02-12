package com.grapesnberries.curllogger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mohamedzakaria on 2/4/16.
 */
public class CurlLoggerInterceptor implements Interceptor {

    private String tag = null;

    public CurlLoggerInterceptor() {

    }

    /**
     * Set logcat tag for curl lib to make it ease to filter curl logs only.
     */
    public CurlLoggerInterceptor(String tag) {
        this.tag = tag;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestPrintingTask task = RequestPrintingTask.obtain(tag, request);
        try {
            task.execute();
        } catch (Throwable throwable) {
            CurlPrinter.printError(tag, throwable);
        } finally {
            task.recycle();
        }
        return chain.proceed(request);
    }
}

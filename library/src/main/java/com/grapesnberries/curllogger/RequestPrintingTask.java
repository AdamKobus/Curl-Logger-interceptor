package com.grapesnberries.curllogger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pools;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

class RequestPrintingTask {

    private static final Pools.SynchronizedPool<RequestPrintingTask> POOL =
            new Pools.SynchronizedPool<>(10);

    @NonNull
    static RequestPrintingTask obtain(@Nullable String tag, @NonNull Request request) {
        RequestPrintingTask task = POOL.acquire();
        if (task == null) {
            task = new RequestPrintingTask();
        }
        task.tag = tag;
        task.request = request;
        return task;
    }

    private final Charset UTF8 = Charset.forName("UTF-8");

    private StringBuilder curlCommandBuilder;
    private Request request;
    private String tag;

    void execute() throws IOException {
        curlCommandBuilder = new StringBuilder("");
        // add cURL command
        curlCommandBuilder.append("cURL ");
        curlCommandBuilder.append("-X ");
        // add method
        curlCommandBuilder.append(request.method().toUpperCase()).append(" ");
        // adding headers
        for (String headerName : request.headers().names()) {
            addHeader(headerName, request.headers().get(headerName));
        }

        // adding request body
        RequestBody requestBody = request.body();
        if (request.body() != null) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                addHeader("Content-Type", request.body().contentType().toString());
                charset = contentType.charset(UTF8);
                curlCommandBuilder.append(" -d '")
                                  .append(buffer.readString(charset))
                                  .append("'");
            }
        }

        // add request URL
        curlCommandBuilder.append(" \"")
                          .append(request.url().toString())
                          .append("\"");
        curlCommandBuilder.append(" -L");

        CurlPrinter.print(tag, request.url().toString(), curlCommandBuilder.toString());
    }

    private void addHeader(String headerName, String headerValue) {
        curlCommandBuilder.append("-H ")
                          .append("\"")
                          .append(headerName)
                          .append(": ")
                          .append(headerValue)
                          .append("\" ");
    }

    void recycle() {
        this.request = null;
        this.tag = null;
        POOL.release(this);
    }
}

package com.globalitians.employees.utility.network;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTPUtils {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static OkHttpClient clientBase = new OkHttpClient();
    public static OkHttpClient client =clientBase.newBuilder()
            .readTimeout(60000, TimeUnit.MILLISECONDS)
            .writeTimeout(60000, TimeUnit.MILLISECONDS)
    .build();

    public static String getRun(String url, Map<String, String> headers) throws IOException {

        Request request = createRequestBuilder(url, null, headers).build();
        Response response = client.newCall(request).execute();

        return "" + response.body().string();
    }

    public static String postRun(String url, String json, Map<String, String> headers) throws IOException {

        Request request = createRequestBuilder(url, json, headers, JSON).build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    public static String postRun(String url, RequestBody formBody, Map<String, String> headers) throws IOException {

        Request request = createRequestBuilder(url, formBody, headers).build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    public static void cancelRequest(String url) throws IOException {

        cancelCallWithTag(client,url);
    }

    public static void cancelCallWithTag(OkHttpClient client, String tag) {
        for (Call call : client.dispatcher().queuedCalls()) {
            if (call.request().tag().equals(tag)) call.cancel();
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (call.request().tag().equals(tag)) call.cancel();
        }
    }

    // Default method to create request builder
    private static Request.Builder createRequestBuilder(String url, RequestBody formBody, Map<String, String> headers) {
        // Create object of request builder
        Request.Builder buildRequest = new Request.Builder();
        buildRequest.url(url).tag(url);

        // Add headers if available
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                buildRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // It only call when there are POST request. For GET, it is not required
        if (formBody != null) {
            buildRequest.post(formBody);
        }

        return buildRequest;
    }

    // When user select MediaType JSON, this method should call
    private static Request.Builder createRequestBuilder(String url, String formBody, Map<String, String> headers, MediaType mediaType) {
        // Create object of request builder
        Request.Builder buildRequest = new Request.Builder();
        buildRequest.url(url).tag(url);

        // Add headers if available
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                buildRequest.addHeader(entry.getKey(), entry.getValue());
            }
        }

        // It only call when there are POST request. For GET, it is not required
        if (formBody != null) {
            RequestBody body = RequestBody.create(mediaType, formBody);
            buildRequest.post(body);
        }

        return buildRequest;
    }
}
package com.darcy.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by csqiang
 * Created on 2017/4/19-19:43.
 * Description:
 */

public class HttpUtils {
    public static void sendOkHttpRequest(String httpUrl, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(httpUrl).build();
        client.newCall(request).enqueue(callback);
    }
}

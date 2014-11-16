package com.nalyutin.util;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Created by pine on 12/11/14.
 */
public class URLReader {

    public static String getData(String urlAddress)
    {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(urlAddress).build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            return "Cannot connect";
        }
    }
}

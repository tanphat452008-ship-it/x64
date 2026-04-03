package com.samp.online.gui;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ImgurUploader {

    private static final String IMGUR_CLIENT_ID = "c7fb3f8d48ae9ca";

    public static void uploadImage(File file, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", file.getName(),
                        RequestBody.create(MediaType.parse("image/png"), file))
                .build();

        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/upload")
                .addHeader("Authorization", "Client-ID " + IMGUR_CLIENT_ID)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public static String parseImageUrlFromResponse(String responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.getJSONObject("data").getString("link");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}



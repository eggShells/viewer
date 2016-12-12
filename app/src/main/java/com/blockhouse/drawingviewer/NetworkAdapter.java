package com.blockhouse.drawingviewer;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by eslaugh on 12/9/2016.
 */

public class NetworkAdapter {

    public static String getWebPageContents(String url) throws IOException{

        OkHttpClient myClient = new OkHttpClient();
        Log.d("tag","make request");
        Request myRequest = new Request.Builder().url(url).build();
        Response myResponse = myClient.newCall(myRequest).execute();
        return myResponse.body().string();
    }
}

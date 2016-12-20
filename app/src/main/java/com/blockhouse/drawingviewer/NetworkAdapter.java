package com.blockhouse.drawingviewer;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Headers;
import okhttp3.HttpUrl;
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

    public static String getScheduledItems(String Department, String date) throws IOException{

        OkHttpClient myClient = new OkHttpClient();
        //String url = "http://192.168.0.172:8888/servlet/";
        HttpUrl url = new HttpUrl.Builder().scheme("http").host("192.168.0.172").port(8888).addPathSegment("servlet").addPathSegment("AndroidServlet").addPathSegment("")
                .addQueryParameter("query","schedule").addQueryParameter("wc","FIN").addQueryParameter("jd",date).build();
        Request myRequest = new Request.Builder().url(url).build();
        Response myResponse = myClient.newCall(myRequest).execute();
        return myResponse.body().string();
    }

    public static byte[] getDocument(String item) throws IOException{

        OkHttpClient myClient = new OkHttpClient();
        //String url = "http://192.168.0.172:8888/servlet/";
        HttpUrl url = new HttpUrl.Builder().scheme("http").host("192.168.0.172").port(8888).addPathSegment("servlet").addPathSegment("AndroidServlet").addPathSegment("")
                .addQueryParameter("get","document").addQueryParameter("item",item).build();
        Log.d("tag", String.valueOf(url));
        Request myRequest = new Request.Builder().url(url).build();
        Response myResponse = myClient.newCall(myRequest).execute();
        byte[] mByteArray = myResponse.body().bytes();

        myResponse.body().close();
        myResponse.close();
        return mByteArray;
    }
}

package com.jim.MelodyPlayer.player;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Jim on 2018/5/4.
 */

public class HttpUtils {

    public static final int READ_TIME_OUT=60000;
    public static final int CONNECT_OUT=60000;

    public static URLConnection openConnection(String request) {
        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL(request);
            urlConnection= (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIME_OUT);
            urlConnection.setConnectTimeout(CONNECT_OUT);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return urlConnection;
        }
    }

}

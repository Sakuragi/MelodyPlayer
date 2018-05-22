package com.jim.MelodyPlayer.player;

import android.text.TextUtils;
import android.util.Log;


import com.jim.MelodyPlayer.utils.LogUtil;
import com.jim.MelodyPlayer.utils.Util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jim on 2018/4/26.
 */

public class Request {

    public static final int READ_TIME_OUT = 60000;
    public static final int CONNECT_OUT = 60000;
    public final static String HOST = "Host";
    public final static String RANGE = "Range";
    public final static String RANGE_PARAMS = "bytes=";
    public final static int HTTP_301 = 301;
    public final static int HTTP_302 = 302;

    private final String TAG = Request.class.getSimpleName();
    private static final Pattern RANGE_HEADER_PATTERN = Pattern.compile("[R,r]ange:[ ]?bytes=(\\d*)-");
    public String requestUrl;
    public String method;
    public long offset;
    private String request;
    int redirectCount = 0;
    private boolean canUseCache;

    public Request(String request) {
        this.request = request;
        initRequestParam(request);
    }

    private void initRequestParam(String request) {
        if (!TextUtils.isEmpty(request)) {
            findUrlAndMethod(request);
            long tempOffset = findRangeOffset(request);
            canUseCache = tempOffset <= 0;
            offset = Math.max(tempOffset, 0);
        }
    }

    private long findRangeOffset(String request) {
        Matcher matcher = RANGE_HEADER_PATTERN.matcher(request);
        if (matcher.find()) {
            String rangeValue = matcher.group(1);
            return Long.parseLong(rangeValue);
        }
        return -1;
    }

    private void findUrlAndMethod(String request) {
        String[] requestParts = request.split("\n");
        StringTokenizer tokenizer = new StringTokenizer(requestParts[0]);
        method = tokenizer.nextToken();
        requestUrl = Util.decodeUrl(tokenizer.nextToken());
        if (requestUrl.startsWith("/")) {
            requestUrl = requestUrl.substring(1);
        }
        Log.d(TAG, "method: " + method + " url: " + requestUrl);
    }

    public URLConnection openConnection() throws IOException {
        LogUtil.d("openConnection");
        HttpURLConnection urlConnection = null;
        String url = requestUrl;
        if (TextUtils.isEmpty(url)) {
            Log.d(TAG, "request url can not be null!");
        }
        LogUtil.d("url: "+requestUrl);
        boolean redirect;
        do {
            urlConnection = (HttpURLConnection) openRealConnection(url,offset);
            int code = urlConnection.getResponseCode();
            redirect = code == HTTP_301 || code == HTTP_302;
            if (redirect) {
                url = urlConnection.getHeaderField("Location");
                urlConnection.disconnect();
                redirectCount++;
                if (redirectCount >= 3) {
                    redirect = false;
                }
            }
        } while (redirect);
        return urlConnection;
    }

    private URLConnection openRealConnection(String requestUrl,long offset) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(requestUrl).openConnection();
        urlConnection.setRequestMethod(method);
        urlConnection.setConnectTimeout(CONNECT_OUT);
        urlConnection.setReadTimeout(READ_TIME_OUT);
        String[] requestParts = request.split("\n");
        for (int i = 1; i < requestParts.length; i++) {
            int separatorLocation = requestParts[i].indexOf(":");
            String name = requestParts[i].substring(0, separatorLocation).trim();
            if (name.equals(HOST)) {
                continue;
            }
            String value = requestParts[i].substring(separatorLocation + 1).trim();
            Log.d(TAG, "name: " + name + " value: " + value);
            urlConnection.setRequestProperty(name, value);
        }
        urlConnection.setRequestProperty(RANGE, RANGE_PARAMS + offset + "-");
        return urlConnection;
    }

    public boolean isCanUseCache() {
        return canUseCache;
    }

    public int getContentLength(){
        try {
            HttpURLConnection urlConnection= (HttpURLConnection) openRealConnection(requestUrl,0);
            return urlConnection.getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

}

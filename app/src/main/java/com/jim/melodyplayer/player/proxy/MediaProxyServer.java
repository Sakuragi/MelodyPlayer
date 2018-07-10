package com.jim.melodyplayer.player.proxy;

import android.text.TextUtils;
import android.util.Log;


import com.jim.melodyplayer.utils.LogUtil;
import com.jim.melodyplayer.utils.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Jim on 2018/4/25 0025.
 */

public class MediaProxyServer {

    private final String TAG = MediaProxyServer.class.getSimpleName();
    private static  String PROXY_HOST = "10.0.2.2";
    private static final int STATE_INIT = 1;

    private ServerSocket mServerSocket;
    private final ExecutorService requestProcessPool = Executors.newFixedThreadPool(4);
    private int port;
    private Thread listenRequestsThread;
    private int currentServerState = -1;

    public MediaProxyServer() {
        init();
    }

    public void init() {
        LogUtil.i("init");
        try {
            CountDownLatch signal = new CountDownLatch(1);
            listenRequestsThread = new Thread(new ListenRequestRunnable(signal));
            listenRequestsThread.start();
            signal.await();
            LogUtil.d(TAG, "listen request thread was started");
        } catch (Exception e) {
            requestProcessPool.shutdown();
            LogUtil.e(TAG, e.toString());
        }

    }


    public String getProxyHostUrl(String url) {
        return String.format(Locale.US, "http://%s:%d/%s", PROXY_HOST, port, Util.encodeUrl(url));
    }

    private class ListenRequestRunnable implements Runnable {

        private CountDownLatch startSignal;

        public ListenRequestRunnable(CountDownLatch signal) {
            startSignal = signal;
        }

        @Override
        public void run() {
            startSignal.countDown();
            InetAddress address = null;
            try {
                PROXY_HOST=InetAddress.getLocalHost().getCanonicalHostName();
                LogUtil.d("proxy host: "+PROXY_HOST);
                mServerSocket = new ServerSocket(0, 8, InetAddress.getLocalHost());
                port = mServerSocket.getLocalPort();
            } catch (Exception e) {
                LogUtil.e(e.toString());
            }
            currentServerState = STATE_INIT;
            waittingForRequests();
        }
    }

    private void waittingForRequests() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                LogUtil.i(TAG, "=======waitting for client connect=======");
                Socket client = mServerSocket.accept();
                LogUtil.i(TAG, "=======client connected=======");
                requestProcessPool.submit(new HandleRequestsRunnable(client));
            }
        } catch (IOException e) {
            LogUtil.e(e.toString());
        }
    }

    private class HandleRequestsRunnable implements Runnable {

        private Socket mClient;

        public HandleRequestsRunnable(Socket client) {
            mClient = client;
        }

        @Override
        public void run() {
            handleRequests(mClient);
        }
    }

    private void handleRequests(Socket client) {
        LogUtil.d("handleRequests");
        try {
            Request request = GetRequest(client);
            processRequest(request, client);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRequest(Request request, Socket client) {
        LogUtil.d("processRequest");
        LogUtil.d("url: " + request.requestUrl);
        FileCache cache = FileCache.open(request.requestUrl);
        byte[] buffer = new byte[1024 * 4];
        int readBytes = -1;
        URLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        try {
            /*第一步，判断本地是否有完整缓存*/
            if (cache.isCompleted()) {
                LogUtil.d("cache completed");
                long offset = request.offset;
                while ((readBytes = cache.read(buffer, offset, buffer.length)) != -1) {
                    client.getOutputStream().write(buffer, 0, readBytes);
                    LogUtil.d("read bytes: " + readBytes);
                    offset += readBytes;
                }
                return;
            }
            /*本地有缓存，但不完整*/
            if (!cache.isCompleted() && cache.available() > request.offset + 2 * 1024 * 1024) {
                LogUtil.d("return small part of cache: " + cache.available());
                while ((readBytes = cache.read(buffer, request.offset, buffer.length)) != -1) {
                    LogUtil.d("read bytes: " + readBytes);
                    client.getOutputStream().write(buffer, 0, readBytes);
                }
            }
            /*联网获取缓存*/
            request.offset = cache.available();
            connection = request.openConnection();
            is = connection.getInputStream();
            while ((readBytes = is.read(buffer, 0, buffer.length)) != -1) {
                client.getOutputStream().write(buffer, 0, readBytes);
                cache.append(buffer, readBytes);
                LogUtil.d("read bytes: " + readBytes);
            }
            if (cache.available() == request.getContentLength() && !cache.isCompleted()) {
                LogUtil.d("cache finished");
                cache.finishedCache();
            }
        } catch (IOException e) {
            LogUtil.e(e.toString());
        } finally {
            Util.closeCloseableQuietly(is);
            Util.closeCloseableQuietly(os);
        }
    }

    private Request GetRequest(Socket client) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        StringBuilder request = new StringBuilder();
        String line = "";
        while (!TextUtils.isEmpty(line = reader.readLine())) {
            request.append(line).append("\n");
        }
//        Util.closeCloseableQuietly(reader);
        return new Request(request.toString());
    }

}

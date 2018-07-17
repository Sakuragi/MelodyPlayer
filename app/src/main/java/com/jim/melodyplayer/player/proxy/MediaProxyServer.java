package com.jim.melodyplayer.player.proxy;

import android.text.TextUtils;

import com.jim.melodyplayer.utils.LogUtil;
import com.jim.melodyplayer.utils.Util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.String.format;

/**
 * Created by Jim on 2018/4/25 0025.
 */

public class MediaProxyServer {

    private final String TAG = MediaProxyServer.class.getSimpleName();
    private static final String PROXY_HOST = "127.0.0.1";
    private static final int STATE_INIT = 1;

    private ServerSocket mServerSocket;
    private final ExecutorService requestProcessPool = Executors.newFixedThreadPool(8);
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
            LogUtil.d("proxy host: " + PROXY_HOST);
            try {
                mServerSocket = new ServerSocket(0, 8, InetAddress.getByName(PROXY_HOST));
                port = mServerSocket.getLocalPort();
                currentServerState = STATE_INIT;
                waittingForRequests();
            } catch (Exception e) {
                LogUtil.e(e.toString());
            }
        }
    }

    private void waittingForRequests() throws IOException {
        while (!Thread.currentThread().isInterrupted()) {
            LogUtil.i(TAG, "=======waitting for client connect=======");
            Socket client = mServerSocket.accept();
            LogUtil.i(TAG, "=======client connected=======");
            requestProcessPool.submit(new HandleRequestsRunnable(client));
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
            Request request = getRequest(client);
            processRequest(request, client);
        } catch (IOException e) {
            LogUtil.e(e.toString());
        } finally {
            try {
                client.shutdownInput();
                client.shutdownOutput();
                client.close();
                LogUtil.i("close client");
            } catch (IOException e) {
                LogUtil.e("close error: " + e);
            }
        }
    }

    private void processRequest(Request request, Socket client) throws IOException {
        LogUtil.d("processRequest");
        LogUtil.d("url: " + request.requestUrl);
        BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
        bos.write(request.getResponseHeaders().getBytes("UTF-8"));
        FileCache cache = FileCache.open(request.requestUrl);
        byte[] buffer = new byte[1024 * 4];
        int readBytes = -1;
        URLConnection connection = null;
        InputStream is = null;
        try {
            /*第一步，判断本地是否有完整缓存*/
            if (cache.isCompleted()) {
                LogUtil.d("cache completed: " + cache.available());
                long offset = request.offset;
                while ((readBytes = cache.read(buffer, offset, buffer.length)) != -1) {
                    bos.write(buffer, 0, readBytes);
//                    LogUtil.d("read bytes: " + readBytes);
                    offset += readBytes;
                }
                return;
            }
            /*本地有缓存，但不完整*/
            if (!cache.isCompleted() && cache.available() > request.offset + 2 * 1024 * 1024) {
                LogUtil.d("return small part of cache: " + cache.available());
                while ((readBytes = cache.read(buffer, request.offset, buffer.length)) != -1) {
//                    LogUtil.d("read bytes: " + readBytes);
                    bos.write(buffer, 0, readBytes);
                }
            }
            /*联网获取缓存*/
            request.offset = cache.available();
            connection = request.openConnection();
            is = new BufferedInputStream(connection.getInputStream(), 8 * 1024 * 1024);
            while ((readBytes = is.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, readBytes);
                if (request.isCanUseCache()) {
                    cache.append(buffer, readBytes);
//                    LogUtil.d("write cache " + readBytes);
                }
//                LogUtil.d("read bytes: " + readBytes);
            }
            if (request.isCanUseCache() && cache.available() == request.getContentLengthValue() && !cache.isCompleted()) {
                LogUtil.d("cache finished: " + cache.available());
                cache.finishedCache();
            }
        } catch (IOException e) {
            LogUtil.e(e.toString());
        } finally {
            Util.closeCloseableQuietly(is);
        }
    }


    private Request getRequest(Socket client) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
        StringBuilder request = new StringBuilder();
        String line = "";
        while (!TextUtils.isEmpty(line = reader.readLine())) {
            request.append(line).append("\n");
        }
//        Util.closeCloseableQuietly(reader);
        return new Request(request.toString());
    }

}

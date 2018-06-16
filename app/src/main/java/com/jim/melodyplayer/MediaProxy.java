package com.jim.melodyplayer;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by Jim on 2018/4/2 0002.
 */

public class MediaProxy {

    private final String TAG = MediaProxy.class.getSimpleName();

    private String localServerAdress = "127.0.0.1";
    private static final int PROXY_PORT = 8123;
    private Socket mSocket;
    private ServerSocket mServerSocket;
    private final String hostName = "https://video.ydlcdn.com/2018/01/25/786b3e1640569ac1379505fdb0f8d1a8.mp3";
    private Thread mThread;
    private SocketListeningRequest mRequest = new SocketListeningRequest();

    public void init() {
            listenLocalRequest();
    }

    private void listenLocalRequest() {
        mThread = new Thread(mRequest);
        mThread.start();
    }

    public String getProxyURL(String url) {
        mRequest.url = url;
        return String.format(Locale.getDefault(), "http://127.0.0.1:%d/%s", PROXY_PORT,"low/luoo/radio889/01.mp3");
    }

    private class SocketListeningRequest implements Runnable {

        public String url;

        @Override
        public void run() {
            try {
                mServerSocket = new ServerSocket(PROXY_PORT, 0, InetAddress.getLocalHost());
                mServerSocket.setSoTimeout(5000);
                if (mServerSocket == null || mServerSocket.isClosed()) {
                    return;
                }

                OutputStream clientOs = null;
                InputStream cacheIs = null;
                Socket client = null;
                while (!Thread.currentThread().isInterrupted()) {
                    Log.d(TAG,"====Waiting for connected=== ");
                    client = mServerSocket.accept();
                    if (client == null) {
                        Log.i(TAG, "client is null");
                        return;
                    }
                    cacheIs=client.getInputStream();
                    Log.d(TAG, "------------client connected--------");
                    byte[] buffer = new byte[1024];
                    int length;
                    String str="";
                    while ((length = cacheIs.read(buffer)) != -1) {
                        str+=new String(buffer);
                        if (str.contains("GET")&&str.contains("\r\n\r\n")){
                            Log.d(TAG,"str: "+str);
                            str=str.replace("127.0.0.1:8123","mp3-cdn.luoo.net");
                            Log.d(TAG,"after str: "+str);
                            String[] requestParts = str.split("\r\n");
                            for (int i=0;i<requestParts.length;i++){
                                Log.d(TAG,"request parts: "+requestParts[i]);
                            }
                            StringTokenizer st = new StringTokenizer(requestParts[0]);
                            String method = st.nextToken();
                            String uri = st.nextToken();
                            Log.d(TAG,"method: "+method+" uri: "+uri);
                            break;
                        }
                    }
                    Socket remoteSocket = sendRemoteRequest(str);
                    resRequest(client, remoteSocket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void resRequest(Socket client, Socket remoteSocket) {
        byte[] buffer = new byte[1024 * 4];

        try {
            while (remoteSocket.getInputStream().read(buffer) != -1) {
                client.getOutputStream().write(buffer);
                client.getOutputStream().flush();
            }
        } catch (IOException e) {
            Log.d(TAG,e.toString());
        }
    }

    private Socket sendRemoteRequest(String requestParams) {
        Socket remoteSocket = null;
        try {
            Log.d(TAG,"remote url: "+mRequest.url);
            InetAddress address = InetAddress.getByName("mp3-cdn.luoo.net");
            Log.d(TAG, "host adress: " + new String(address.getAddress(),"UTF-8"));
            Log.d(TAG, "host name: " + address.getHostName());
            remoteSocket = new Socket();
            remoteSocket.connect(new InetSocketAddress(address.getHostName(), 80));
            remoteSocket.getOutputStream().write(
                    requestParams.getBytes("UTF-8"));
            remoteSocket.getOutputStream().flush();
        } catch (IOException e) {
            Log.d(TAG,e.toString());
        }
        return remoteSocket;
    }


    public String convertRequest(Socket client, String remoteHost) {
        byte[] buffer = new byte[2048];
        String params = "";
        try {
            int readBytes;
            while ((readBytes = client.getInputStream().read(buffer)) == -1) {
                params += new String(buffer, "UTF-8");
                if (params.contains("GET") && params.contains("\r\n\r\n")) {
                    Log.d(TAG, "param: " + params);
                    params.replace("127.0.0.1", "video.ydlcdn.com");
                    break;
                }
            }
            Log.d(TAG, "param: " + params);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return params;
    }

//    private Socket sendRemoteRequest(HostInfo remoteInfo) {
//        Socket remoteSocket = null;
//        remoteSocket =new Socket();
//        remoteSocket.connect();
//    }

}

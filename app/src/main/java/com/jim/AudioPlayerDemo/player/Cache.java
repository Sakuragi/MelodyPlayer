package com.jim.AudioPlayerDemo.player;

import com.jim.videoplayerdemo.App;

import java.io.IOException;

/**
 * Created by Jim on 2018/5/15 0015.
 */

public interface Cache {
    String cachePath= App.app.getExternalCacheDir()+"buffers/";
    int read(byte b[]) throws IOException;
    int read(byte b[], long off, int len) throws IOException;
    long available() throws IOException;
    void close() throws IOException;
    void append(byte[] data, int length) throws IOException;
    boolean isCompleted();
    void finishedCache();
}

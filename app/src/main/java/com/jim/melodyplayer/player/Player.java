package com.jim.melodyplayer.player;

/**
 * Created by Jim on 2018/5/27.
 */
public interface Player {
    void open(String url);
    void play();
    void playOrPause();
    void seek(int position);
    void stop();
    void destroy();
    long getDuration();
}

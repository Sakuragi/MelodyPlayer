package com.jim.MelodyPlayer.player;

/**
 * Created by Jim on 2018/5/25 0025.
 */

public interface Player {
    void play();
    void playOrPause();
    void seek(int position);
    void open(String url);
    void stop();
    void release();
    long getDurtion();
}

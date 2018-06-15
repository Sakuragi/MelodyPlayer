package com.jim.MelodyPlayer.player;

import android.media.MediaPlayer;
import android.util.Log;

import com.jim.MelodyPlayer.MediaProxy;

import java.io.IOException;

/**
 * Created by Jim on 2018/1/29 0029.
 */

public class AudioPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, Player,
        MediaPlayer.OnPreparedListener {

    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    
    private int currentState=0;

    private MediaPlayer mMediaPlayer;



    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void play() {

    }

    @Override
    public void playOrPause() {

    }

    @Override
    public void seek(int position) {

    }

    @Override
    public void open(String url) {
        mMediaPlayer=new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.prepareAsync();
        currentState=STATE_PREPARING;
    }

    @Override
    public void stop() {

    }

    @Override
    public void release() {

    }

    @Override
    public long getDurtion() {
        return 0;
    }
}

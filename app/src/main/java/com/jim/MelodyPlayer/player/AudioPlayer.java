package com.jim.MelodyPlayer.player;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Jim on 2018/1/29 0029.
 */

public class AudioPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener {


    private final String TAG=AudioPlayer.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    private String testUrl="https://video.ydlcdn.com/2018/01/25/786b3e1640569ac1379505fdb0f8d1a8.mp3";
    private MediaProxy mMediaProxy;

    public AudioPlayer(){
        mMediaProxy=new MediaProxy();
        mMediaProxy.init();
    }

    public void play() {
        if (mMediaPlayer!=null&&!mMediaPlayer.isPlaying()){
            Log.d(TAG,"start");
            mMediaPlayer.start();
        }
    }

    public void pause() {
        if (mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
    }

    public void stop(){
        if (mMediaPlayer!=null&&mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
    }

    public void release(){
        if (mMediaPlayer!=null){
            mMediaPlayer.release();
        }
    }

    public void setDataSource(String url) {
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mMediaProxy.getProxyURL(url));
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnCompletionListener(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG,"onPrepared");
        play();
    }
}

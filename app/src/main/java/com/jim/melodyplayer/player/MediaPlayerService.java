package com.jim.melodyplayer.player;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Jim on 2018/5/22 0022.
 */

public class MediaPlayerService extends Service implements Player{

    private AudioPlayer mPlayer;
    private final Binder mBinder = new LocalBinder();

    @Override
    public void open(String url) {
        mPlayer.open(url);
    }

    @Override
    public void play() {
        mPlayer.playOrPause();
    }

    @Override
    public void playOrPause() {
        mPlayer.playOrPause();
    }

    @Override
    public void seek(int position) {
        mPlayer.seek(position);
    }

    @Override
    public void stop() {
        mPlayer.stop();
    }

    @Override
    public void destroy() {

    }

    @Override
    public long getDuration() {
        return mPlayer.getDuration();
    }

    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer=new AudioPlayer(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


}

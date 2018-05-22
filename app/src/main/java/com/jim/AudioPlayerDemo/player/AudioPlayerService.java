package com.jim.AudioPlayerDemo.player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.jim.AudioPlayerDemo.IMyAidlInterface;

import java.io.IOException;

/**
 * Created by Jim on 2018/3/22 0022.
 */

public class AudioPlayerService extends Service {

    private final String TAG=AudioPlayerService.class.getSimpleName();
    private IBinder mIBinder = new ServiceStub();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mIBinder;
    }

    private class ServiceStub extends IMyAidlInterface.Stub {


        @Override
        public void play() throws RemoteException {

        }

        @Override
        public void next() throws RemoteException {

        }

        @Override
        public void pause() throws RemoteException {

        }

        @Override
        public void stop() throws RemoteException {

        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return false;
        }

        @Override
        public void setDataSource(String url) throws RemoteException {

        }

        @Override
        public long seekTo(long position) throws RemoteException {
            return 0;
        }

        @Override
        public void exit() throws RemoteException {

        }
    }

    class MutiPlayer implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener{
        private MediaPlayer mCurrentPlayer = new MediaPlayer();
        private MediaPlayer mNextPlayer;

        public MutiPlayer() {

        }

        public void start(String url) {
            mCurrentPlayer.start();
        }

        public void setDataSource(String url) {
            if (TextUtils.isEmpty(url)){
                Log.i(TAG,"no data source");
                return;
            }
            try {
                mCurrentPlayer.setDataSource(url);
                mCurrentPlayer.prepareAsync();
                mCurrentPlayer.setOnPreparedListener(this);
                mCurrentPlayer.setOnCompletionListener(this);
                mCurrentPlayer.setOnErrorListener(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) {

        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e(TAG,"MediaPlay error");
            return false;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {

        }
    }
}

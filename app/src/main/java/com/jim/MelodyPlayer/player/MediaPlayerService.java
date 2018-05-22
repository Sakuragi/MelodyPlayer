package com.jim.MelodyPlayer.player;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.jim.MelodyPlayer.IMyAidlInterface;

import java.lang.ref.WeakReference;

/**
 * Created by Jim on 2018/5/22 0022.
 */

public class MediaPlayerService extends Service {

    private IBinder mIBinder=new AudioPlayerServiceStub(this);


    public void play(){

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class AudioPlayerServiceStub extends IMyAidlInterface.Stub{

        private WeakReference<MediaPlayerService> mService;

        public AudioPlayerServiceStub(MediaPlayerService service){
            mService=new WeakReference<MediaPlayerService>(service);
        }

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
}

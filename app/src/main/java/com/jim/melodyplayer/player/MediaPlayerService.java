package com.jim.melodyplayer.player;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.jim.melodyplayer.model.SongInfoBean;

import java.util.List;

/**
 * Created by Jim on 2018/5/22 0022.
 */

public class MediaPlayerService extends Service implements Player,PlayerCallBack{

    private AudioPlayer mPlayer;
    private final Binder mBinder = new LocalBinder();

    @Override
    public void updateBuffer(int percent) {

    }

    @Override
    public void onSwitchPrev(SongInfoBean.BitrateEntity song) {

    }

    @Override
    public void onSwitchNext(SongInfoBean.BitrateEntity song) {

    }

    @Override
    public void onComplete(SongInfoBean.BitrateEntity song) {

    }

    @Override
    public void onPlayStateChanged(boolean isPlaying) {

    }

    @Override
    public void onStatePaly(SongInfoBean.BitrateEntity song) {

    }

    @Override
    public void onStateStop(SongInfoBean.BitrateEntity song) {

    }


    public class LocalBinder extends Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    public void play(SongInfoBean.BitrateEntity songInfo){
        mPlayer.play(songInfo);
    }

    public void playAll(List<SongInfoBean.BitrateEntity> songs, int index){
        mPlayer.playAll(songs,index);
    }

    public void pause(){
        mPlayer.pause();
    }

    public void seek(int position) {
        mPlayer.seek(position);
    }

    public void stop() {
        mPlayer.stop();
    }

    public void destroy() {
        mPlayer.destroy();
    }

    public long getDuration() {
        return mPlayer.getDuration();
    }

    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }

    public void playNext(){
        mPlayer.playNext();
    }

    public void playPrev(){
        mPlayer.playPrev();
    }

    public boolean hasNext(){
        return mPlayer.hasNext();
    }

    public boolean hasPrev(){
        return mPlayer.hasPrev();
    }

    public void registerCallBack(PlayerCallBack callBack){
        mPlayer.registerCallBack(callBack);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer=AudioPlayer.getInstance(this);
        mPlayer.registerCallBack(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


}

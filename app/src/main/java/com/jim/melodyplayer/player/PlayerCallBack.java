package com.jim.melodyplayer.player;

import com.jim.melodyplayer.model.SongInfoBean;

/**
 * Created by Jim on 2018/7/3.
 */
public interface PlayerCallBack {
    void updateBuffer(int percent);

    void onSwitchPrev(SongInfoBean.BitrateEntity song);

    void onSwitchNext(SongInfoBean.BitrateEntity song);

    void onComplete();

    void onPlayStateChanged(boolean isPlaying);

    void onStatePlay(SongInfoBean.BitrateEntity song);

    void onStateStop(SongInfoBean.BitrateEntity song);

    void onProgressUpdate(int progress);
}

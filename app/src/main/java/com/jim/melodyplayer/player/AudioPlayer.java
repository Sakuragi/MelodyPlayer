package com.jim.melodyplayer.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

import com.jim.melodyplayer.model.SongInfoBean;
import com.jim.melodyplayer.player.proxy.MediaProxyServer;
import com.jim.melodyplayer.utils.LogUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

/**
 * Created by Jim on 2018/1/29 0029.
 */

public class AudioPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, Player,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener {
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    public static final int INIT_SEEK_BAR = 1;
    public static final int UPDATE_SEEK_BAR = 2;

    private final String TAG = AudioPlayer.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    private MediaProxyServer mMediaProxy;
    private int mCurrentState = STATE_IDLE;
    private AudioManager mAudioManager;
    private WeakReference<Context> mContext;
    private ArrayList<SongInfoBean.BitrateEntity> playList;
    private ArrayList<PlayerCallBack> mCallBacks;
    private static AudioPlayer sAudioPlayer;
    private int mPosition;


    public static AudioPlayer getInstance(Context context) {
        if (sAudioPlayer == null) {
            synchronized (AudioPlayer.class) {
                if (sAudioPlayer == null) {
                    sAudioPlayer = new AudioPlayer(context);
                }
            }
        }
        return sAudioPlayer;
    }


    private AudioPlayer(Context context) {
        mContext = new WeakReference<>(context);
        playList = new ArrayList<>();
        mCallBacks = new ArrayList<>();
        mMediaProxy = new MediaProxyServer();
        mMediaProxy.init();
        mMediaPlayer = new MediaPlayer();
        mPosition = 0;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        notifyComplete(playList.get(mPosition));
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mCurrentState == STATE_PREPARING) {
            mCurrentState = STATE_PREPARED;
            startPlayer();
        }
    }


    public void play(SongInfoBean.BitrateEntity songInfo) {
        if (songInfo == null) {
            LogUtil.e("song info can not be null!");
            return;
        }
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            notifyPlayStateChanged(true);
            return;
        }
        playList.add(songInfo);
        mPosition = playList.size();
        open(songInfo.getFile_link());
        if (!mMediaPlayer.isPlaying() && !isCanNotPlay()) {
            startPlayer();
        }
    }

    public void playAll(List<SongInfoBean.BitrateEntity> songs, int index) {
        if (songs == null || songs.size() <= 0) {
            LogUtil.e("songs info can not be null!");
            return;
        }
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            notifyPlayStateChanged(true);
            return;
        }
        playList.addAll(songs);
        mPosition = index;
        open(songs.get(index).getFile_link());
        if (!mMediaPlayer.isPlaying() && !isCanNotPlay()) {
            startPlayer();
        }
    }

    private void startPlayer() {
        LogUtil.i("startPlayer");
        mAudioManager = (AudioManager) mContext.get().getSystemService(Context.AUDIO_SERVICE);
        int status = mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (status == AUDIOFOCUS_REQUEST_GRANTED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            notifyPlay(playList.get(mPosition));
        }
    }


    private void open(String url) {
        LogUtil.i("open url: " + url);
        mMediaPlayer.reset();
        mCurrentState = STATE_IDLE;
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);
        try {
            mMediaPlayer.setDataSource(mMediaProxy.getProxyHostUrl(url));
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
        } catch (IOException e) {
            LogUtil.e("open file error: " + e.toString());
        }
    }

    public void playNext() {
        if (playList != null && playList.size() > 0 && mPosition < playList.size() - 1) {
            mCurrentState = STATE_IDLE;
            mPosition = mPosition + 1;
            play(playList.get(mPosition));
            notifyPlayNext(playList.get(mPosition));
        }
    }

    public void playPrev() {
        if (playList != null && playList.size() > 0 && mPosition >= 1) {
            mCurrentState = STATE_IDLE;
            mPosition = mPosition - 1;
            play(playList.get(mPosition));
            notifyPlayPrev(playList.get(mPosition));
        }
    }

    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            mAudioManager.abandonAudioFocus(mAudioFocusListener);
            notifyPlayStateChanged(false);
        }
    }


    public void seek(int position) {
        if (!isCanNotPlay()) {
            mMediaPlayer.seekTo(position);
        }
    }

    public void stop() {
        if (mCurrentState != STATE_IDLE) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mCurrentState = STATE_IDLE;
            notifyStop(playList.get(mPosition));
        }
    }

    public void destroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mCurrentState = STATE_IDLE;
        }
    }

    public void registerCallBack(PlayerCallBack callBack) {
        mCallBacks.add(callBack);
    }

    public boolean hasNext() {
        return mPosition < playList.size() - 1;
    }

    public boolean hasPrev() {
        return mPosition >= 1;
    }

    public long getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return -1;
    }

    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    private boolean isCanNotPlay() {
        return mCurrentState == STATE_IDLE ||
                mCurrentState == STATE_ERROR ||
                mCurrentState == STATE_PREPARING;
    }

    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(final int focusChange) {
        }
    };

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        notifyBufferUpdate(percent);
    }


    private void notifyBufferUpdate(int percent) {
        for (PlayerCallBack callBack : mCallBacks) {
            callBack.updateBuffer(percent);
        }
    }

    private void notifyPlayPrev(SongInfoBean.BitrateEntity song) {
        for (PlayerCallBack callback : mCallBacks) {
            callback.onSwitchPrev(song);
        }
    }

    private void notifyPlayNext(SongInfoBean.BitrateEntity song) {
        for (PlayerCallBack callback : mCallBacks) {
            callback.onSwitchNext(song);
        }
    }

    private void notifyComplete(SongInfoBean.BitrateEntity song) {
        for (PlayerCallBack callback : mCallBacks) {
            callback.onComplete(song);
        }
    }

    private void notifyPlayStateChanged(boolean isPlaying) {
        for (PlayerCallBack callback : mCallBacks) {
            callback.onPlayStateChanged(isPlaying);
        }
    }

    private void notifyPlay(SongInfoBean.BitrateEntity song) {
        for (PlayerCallBack callback : mCallBacks) {
            callback.onStatePaly(song);
        }
    }

    private void notifyStop(SongInfoBean.BitrateEntity song) {
        for (PlayerCallBack callback : mCallBacks) {
            callback.onStateStop(song);
        }
    }


}

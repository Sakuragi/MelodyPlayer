package com.jim.melodyplayer.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

import com.jim.melodyplayer.model.SongInfoBean;
import com.jim.melodyplayer.utils.LogUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
import static com.jim.melodyplayer.ui.activities.MainActivity.INIT_SEEK_BAR;

/**
 * Created by Jim on 2018/1/29 0029.
 */

public class AudioPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, Player,
        MediaPlayer.OnPreparedListener {
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;

    private final String TAG = AudioPlayer.class.getSimpleName();
    private MediaPlayer mMediaPlayer;
    private MediaProxyServer mMediaProxy;
    private int mCurrentState = STATE_IDLE;
    private Handler uiHandler;
    private AudioManager mAudioManager;
    private WeakReference<Context> mContext;
    private ArrayList<SongInfoBean.BitrateEntity> playList;
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
        mMediaProxy = new MediaProxyServer();
        mMediaProxy.init();
        mMediaPlayer = new MediaPlayer();
        mPosition = 0;
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
        if (mCurrentState == STATE_PREPARING) {
            mCurrentState = STATE_PREPARED;
            notifyUi(uiHandlerObtainMsg());
            startPlayer();
        }
    }

    private void notifyUi(Message message) {
        if (uiHandler == null && message != null) {
            return;
        }
        uiHandler.sendMessage(message);
    }

    private Message uiHandlerObtainMsg(){
        if (uiHandler!=null){
            return uiHandler.obtainMessage();
        }
        return null;
    }

    public void setUiHandler(Handler handler) {
        this.uiHandler = handler;
    }

    private void play(SongInfoBean.BitrateEntity songInfo) {
        if (songInfo == null) {
            LogUtil.e("song info can not be null!");
            return;
        }
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            return;
        }
        playList.add(songInfo);
        mPosition = playList.size();
        open(songInfo.getFile_link());
        if (!mMediaPlayer.isPlaying() && !isCanNotPlay()) {
            startPlayer();
        }
    }

    private void playAll(List<SongInfoBean.BitrateEntity> songs, int index) {
        if (songs == null || songs.size() <= 0) {
            LogUtil.e("songs info can not be null!");
            return;
        }
        if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
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
        mAudioManager = (AudioManager) mContext.get().getSystemService(Context.AUDIO_SERVICE);
        int status = mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (status == AUDIOFOCUS_REQUEST_GRANTED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
    }


    private void open(String url) {
        mMediaPlayer.reset();
        mCurrentState = STATE_IDLE;
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
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

    private void pause() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            mAudioManager.abandonAudioFocus(mAudioFocusListener);
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
        }
    }

    public void destroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mCurrentState = STATE_IDLE;
        }
    }

    public long getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        }
        return -1;
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
}

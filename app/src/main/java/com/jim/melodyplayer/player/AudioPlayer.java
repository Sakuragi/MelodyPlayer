package com.jim.melodyplayer.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

import com.jim.melodyplayer.utils.LogUtil;

import java.io.IOException;

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
    private Handler uiHandler=new Handler();
    private AudioManager mAudioManager;
    private Context mContext;

    public AudioPlayer(Context context) {
        mContext=context;
        mMediaProxy = new MediaProxyServer();
        mMediaProxy.init();
        mMediaPlayer = new MediaPlayer();
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
            Message msg=uiHandler.obtainMessage();
            msg.what=INIT_SEEK_BAR;
            msg.arg1= (int) getDuration();
            play();
        }
    }

    @Override
    public void open(String url) {
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        try {
            mMediaPlayer.setDataSource(mMediaProxy.getProxyHostUrl(url));
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
        } catch (IOException e) {
            LogUtil.e("open file error: "+e.toString());
        }
    }

    @Override
    public void play() {
        if (!mMediaPlayer.isPlaying() &&!isCanNotPlay()) {
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            int status = mAudioManager.requestAudioFocus(mAudioFocusListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (status==AUDIOFOCUS_REQUEST_GRANTED){
                mMediaPlayer.start();
                mCurrentState = STATE_PLAYING;
            }
        }
    }

    @Override
    public void playOrPause() {
        if (isCanNotPlay()){
            return;
        }
        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
            mCurrentState=STATE_PAUSED;
        }else if (!mMediaPlayer.isPlaying()){
            play();
        }
    }

    @Override
    public void seek(int position) {
        if (!isCanNotPlay()){
            mMediaPlayer.seekTo(position);
        }
    }

    @Override
    public void stop() {
        if (mCurrentState!=STATE_IDLE){
            mMediaPlayer.stop();
            mCurrentState=STATE_IDLE;
        }
    }

    @Override
    public void destroy() {
        if (mMediaPlayer!=null){
            mMediaPlayer.release();
        }
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    private boolean isCanNotPlay(){
        return mCurrentState==STATE_IDLE||
                mCurrentState==STATE_ERROR||
                mCurrentState==STATE_PREPARING;
    }

    private final AudioManager.OnAudioFocusChangeListener mAudioFocusListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(final int focusChange) {
        }
    };
}

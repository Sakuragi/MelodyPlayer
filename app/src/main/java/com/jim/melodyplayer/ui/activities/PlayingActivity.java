package com.jim.melodyplayer.ui.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jim.melodyplayer.R;
import com.jim.melodyplayer.app.App;
import com.jim.melodyplayer.model.SongInfoBean;
import com.jim.melodyplayer.net.NetUtils;
import com.jim.melodyplayer.net.SongRequest;
import com.jim.melodyplayer.player.MediaPlayerService;
import com.jim.melodyplayer.player.PlayMode;
import com.jim.melodyplayer.player.PlayerCallBack;
import com.jim.melodyplayer.utils.LogUtil;
import com.jim.melodyplayer.utils.SharedPreferencesEditor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.jim.melodyplayer.player.PlayMode.LIST;

/**
 * Created by Jim on 2018/6/26.
 */
public class PlayingActivity extends AppCompatActivity implements App.ServiceBindCallBack, PlayerCallBack, SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.image_view_album)
    ImageView mImageViewAlbum;
    @BindView(R.id.text_view_name)
    TextView mTextViewName;
    @BindView(R.id.text_view_artist)
    TextView mTextViewArtist;
    @BindView(R.id.text_view_progress)
    TextView mTextViewProgress;
    @BindView(R.id.seek_bar)
    AppCompatSeekBar mSeekBar;
    @BindView(R.id.text_view_duration)
    TextView mTextViewDuration;
    @BindView(R.id.layout_progress)
    LinearLayout mLayoutProgress;
    @BindView(R.id.button_play_mode_toggle)
    AppCompatImageView mButtonPlayModeToggle;
    @BindView(R.id.button_play_last)
    AppCompatImageView mButtonPlayLast;
    @BindView(R.id.button_play_toggle)
    AppCompatImageView mButtonPlayToggle;
    @BindView(R.id.button_play_next)
    AppCompatImageView mButtonPlayNext;
    @BindView(R.id.button_favorite_toggle)
    AppCompatImageView mButtonFavoriteToggle;
    @BindView(R.id.layout_play_controls)
    LinearLayout mLayoutPlayControls;
    private String songId;
    private MediaPlayerService mPlayerService;
    private SongInfoBean.BitrateEntity songInfo;
    private Runnable progressRunnable;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mPlayerService = ((MediaPlayerService.LocalBinder) iBinder).getService();
            mPlayerService.setPlayMode(PlayMode.getCurrentMode());
            mPlayerService.registerCallBack(PlayingActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtil.i("onServiceDisconnected");
            mPlayerService = null;
        }
    };

    public static void start(int songId, Activity activity) {
        Intent i = new Intent(activity, PlayingActivity.class);
        i.putExtra("song_id", songId);
        activity.startActivity(i);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_playing);
        LogUtil.i("onCreate");
        ButterKnife.bind(this);
        App.app.registerBindCallBack(this);
        mPlayerService = App.app.getPlayerService();
        if (mPlayerService!=null){
            mPlayerService.setPlayMode(PlayMode.getCurrentMode());
            mPlayerService.registerCallBack(PlayingActivity.this);
        }
        init();
        loadData();
    }

    private void init() {
        updatePlayMode(PlayMode.getCurrentMode());
        songId = getIntent().getIntExtra("song_id", 0) + "";
        Intent i = new Intent(PlayingActivity.this, MediaPlayerService.class);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    @OnClick({R.id.button_play_mode_toggle, R.id.button_play_toggle, R.id.button_play_next, R.id.button_play_last})
    void doClick(View view) {
        switch (view.getId()) {
            case R.id.button_play_toggle:
                if (mPlayerService != null) {
                    if (mPlayerService.isPlaying()) {
                        mPlayerService.pause();
                    } else {
                        mPlayerService.play(songInfo);
                    }
                }
                break;
            case R.id.button_play_next:
                if (mPlayerService != null) {
                    mPlayerService.playNext();
                }
                break;
            case R.id.button_play_last:
                if (mPlayerService != null) {
                    mPlayerService.playPrev();
                }
                break;
            case R.id.button_play_mode_toggle:
                if (mPlayerService == null) return;
                PlayMode current = PlayMode.getCurrentMode();
                PlayMode newMode = PlayMode.switchNextMode(current);
                mPlayerService.setPlayMode(newMode);
                PlayMode.setPlayMode(newMode);
                updatePlayMode(newMode);
                break;
        }
    }

    private void updatePlayMode(PlayMode newMode) {
        switch (newMode) {
            case LIST:
                mButtonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_list);
                break;
            case LOOP:
                mButtonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_loop);
                break;
            case SHUFFLE:
                mButtonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_shuffle);
                break;
            case SINGLE:
                mButtonPlayModeToggle.setImageResource(R.drawable.ic_play_mode_single);
                break;
        }
    }

    private void loadData() {
        if (songId.equals("0") && mPlayerService.isPlaying()) {
            mButtonPlayToggle.setSelected(true);
            mTextViewName.setText(mPlayerService.getCurrentSong().title);
            mTextViewArtist.setText(mPlayerService.getCurrentSong().author);
            mTextViewDuration.setText(formatDuration(mPlayerService.getCurrentSong().getFile_duration()));
            mTextViewProgress.setText(formatDuration(mPlayerService.getCurrentProgress()));
            return;
        }
        SongRequest songRequest = new SongRequest();
        songRequest.songid = songId;
        NetUtils.fetchSong(songRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<SongInfoBean>() {
                    @Override
                    public void call(SongInfoBean songInfoBean) {
                        LogUtil.e(songInfoBean.getSonginfo().getArtist_1000_1000().split("@")[0]);
                        songInfo = songInfoBean.getBitrate();
                        songInfo.author = songInfoBean.getSonginfo().getAuthor();
                        songInfo.title = songInfoBean.getSonginfo().getTitle();
//                        songInfo.cover=songInfoBean.getSonginfo().
                        mTextViewName.setText(songInfo.title);
                        mTextViewArtist.setText(songInfo.author);
                        mTextViewDuration.setText(formatDuration(songInfo.getFile_duration()));
                        mPlayerService.play(songInfo);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtil.e(throwable.toString());
                    }
                });
    }


    @Override
    public void updateBuffer(int percent) {
        LogUtil.i("update buffer percent: " + percent);
    }

    @Override
    public void onSwitchPrev(SongInfoBean.BitrateEntity song) {
        mTextViewName.setText(song.title);
        mTextViewArtist.setText(song.author);
        LogUtil.i("onSwitchPrev");
        mTextViewDuration.setText(formatDuration(song.getFile_duration()));
    }

    @Override
    public void onSwitchNext(SongInfoBean.BitrateEntity song) {
        mTextViewName.setText(song.title);
        mTextViewArtist.setText(song.author);
        mTextViewDuration.setText(formatDuration(song.getFile_duration()));
    }

    @Override
    public void onComplete() {
        mSeekBar.setProgress(0);
        mTextViewProgress.setText(formatDuration(0));
        mButtonPlayToggle.setSelected(false);
        mPlayerService.seek(-1);
//        mPlayerService.stop();
    }

    @Override
    public void onPlayStateChanged(boolean isPlaying) {
        if (isPlaying) {
            mButtonPlayToggle.setSelected(true);
        } else {
            mButtonPlayToggle.setSelected(false);
        }
    }

    @Override
    public void onStatePlay(SongInfoBean.BitrateEntity song) {
        mButtonPlayToggle.setSelected(true);
    }

    @Override
    public void onStateStop(SongInfoBean.BitrateEntity song) {
    }

    //进度条回调更新
    @Override
    public void onProgressUpdate(int progress) {
        LogUtil.d("progress: " + progress + " duration: " + mPlayerService.getCurrentSong().getFile_duration());
        float seekBarProgress = (float) progress / mPlayerService.getCurrentSong().getFile_duration() * mSeekBar.getMax();
        LogUtil.d("seekbar progress: " + seekBarProgress);
        mSeekBar.setProgress((int) seekBarProgress);
        mTextViewProgress.setText(formatDuration(progress));
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            int targetDuration = (int) (songInfo.getFile_duration() * ((float) progress / mSeekBar.getMax()));
            mTextViewProgress.setText(formatDuration(targetDuration));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int position = (int) (songInfo.getFile_duration() * ((float) seekBar.getProgress() / mSeekBar.getMax()));
        mPlayerService.seek(position * 1000);
    }

    @SuppressLint("DefaultLocale")
    public static String formatDuration(int duration) {
        int minute = duration / 60;
        int hour = minute / 60;
        minute %= 60;
        int second = duration % 60;
        if (hour != 0)
            return String.format("%2d:%02d:%02d", hour, minute, second);
        else
            return String.format("%02d:%02d", minute, second);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (checkNotNull(mPlayerService)) {
            mPlayerService.unRegisterCallBack(this);
        }
    }

    public boolean checkNotNull(Object... objects) {
        for (Object obj : objects) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onServiceBind() {
        if (mPlayerService == null) {
            mPlayerService=App.app.getPlayerService();
            mPlayerService.setPlayMode(PlayMode.getCurrentMode());
            mPlayerService.registerCallBack(PlayingActivity.this);
        }
    }
}

package com.jim.melodyplayer.ui.activities;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jim.melodyplayer.R;
import com.jim.melodyplayer.model.SongInfoBean;
import com.jim.melodyplayer.net.NetUtils;
import com.jim.melodyplayer.net.SongRequest;
import com.jim.melodyplayer.player.AudioPlayer;
import com.jim.melodyplayer.player.MediaPlayerService;
import com.jim.melodyplayer.utils.LogUtil;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Jim on 2018/6/26.
 */
public class PlayingActivity extends AppCompatActivity {

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
    private Handler uiHandler=new UIHandler(this);

    private ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mPlayerService= ((MediaPlayerService.LocalBinder) iBinder).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mPlayerService=null;
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
        ButterKnife.bind(this);
        songId = getIntent().getIntExtra("song_id",0)+"";
        Intent i=new Intent(PlayingActivity.this,MediaPlayerService.class);
        bindService(i,mServiceConnection, Context.BIND_AUTO_CREATE);
        loadData();
    }

    @OnClick({R.id.button_play_toggle,R.id.button_play_next,R.id.button_play_last})
    void doClick(View view){
        switch (view.getId()){
            case R.id.button_play_toggle:
                if (mPlayerService!=null){
                    if (mPlayerService.isPlaying()){
                        mPlayerService.pause();
                    }else {
                        mPlayerService.play(songInfo);
                    }
                }
                break;
            case R.id.button_play_next:
                if (mPlayerService!=null){
                    mPlayerService.playNext();
                }
                break;
            case R.id.button_play_last:
                if (mPlayerService!=null){
                    mPlayerService.playPrev();
                }
                break;
        }
    }

    private void loadData() {
        SongRequest songRequest = new SongRequest();
        songRequest.songid = songId;
        NetUtils.fetchSong(songRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<SongInfoBean>() {
                    @Override
                    public void call(SongInfoBean songInfoBean) {
                        LogUtil.e(songInfoBean.getSonginfo().getArtist_1000_1000().split("@")[0]);
                        mTextViewName.setText(songInfoBean.getSonginfo().getTitle());
                        mTextViewArtist.setText(songInfoBean.getSonginfo().getCompose());
                        songInfo=songInfoBean.getBitrate();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    static class UIHandler extends Handler{

        private WeakReference<PlayingActivity> mActivity;

        public UIHandler(PlayingActivity activity){
            mActivity=new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}

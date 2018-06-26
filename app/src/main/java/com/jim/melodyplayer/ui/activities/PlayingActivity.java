package com.jim.melodyplayer.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatSeekBar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jim.melodyplayer.R;
import com.jim.melodyplayer.model.SonInfoBean;
import com.jim.melodyplayer.model.SongListBean;
import com.jim.melodyplayer.net.NetUtils;
import com.jim.melodyplayer.net.SongRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
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
        loadData();
    }

    private void loadData() {
        SongRequest songRequest = new SongRequest();
        songRequest.songid = songId;
        NetUtils.fetchSong(songRequest)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<SonInfoBean>() {
                    @Override
                    public void call(SonInfoBean songListBean) {
                        mImageViewAlbum.setImageURI(Uri.parse(songListBean.getSonginfo().getArtist_1000_1000().split("@")[0]));
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }
}

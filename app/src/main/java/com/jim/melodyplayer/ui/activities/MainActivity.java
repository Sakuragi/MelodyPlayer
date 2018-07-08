package com.jim.melodyplayer.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jim.melodyplayer.R;
import com.jim.melodyplayer.app.App;
import com.jim.melodyplayer.model.SongInfoBean;
import com.jim.melodyplayer.player.MediaPlayerService;
import com.jim.melodyplayer.player.PlayMode;
import com.jim.melodyplayer.player.PlayerCallBack;
import com.jim.melodyplayer.ui.fragments.LocalFragment;
import com.jim.melodyplayer.ui.fragments.MusicFragment;
import com.jim.melodyplayer.ui.fragments.PlayListFragment;
import com.jim.melodyplayer.ui.fragments.SettingFragment;
import com.jim.melodyplayer.utils.LogUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements PlayerCallBack, App.ServiceBindCallBack {

    @BindView(R.id.radio_button_play_list)
    RadioButton mRadioButtonPlayList;
    @BindView(R.id.radio_button_music_lib)
    RadioButton mRadioButtonMusicLib;
    @BindView(R.id.radio_button_local_files)
    RadioButton mRadioButtonLocalFiles;
    @BindView(R.id.radio_button_settings)
    RadioButton mRadioButtonSettings;
    @BindView(R.id.iv_play_bar_cover)
    ImageView mIvPlayBarCover;
    @BindView(R.id.tv_play_bar_title)
    TextView mTvPlayBarTitle;
    @BindView(R.id.tv_play_bar_artist)
    TextView mTvPlayBarArtist;
    @BindView(R.id.iv_play_bar_play)
    ImageView mIvPlayBarPlay;
    @BindView(R.id.iv_play_bar_next)
    ImageView mIvPlayBarNext;
    @BindView(R.id.v_play_bar_playlist)
    ImageView mVPlayBarPlaylist;
    @BindView(R.id.pb_play_bar)
    ProgressBar mPbPlayBar;
    private String testUrl = "https://video.ydlcdn.com/2018/01/25/786b3e1640569ac1379505fdb0f8d1a8.mp3";
    private final int DEFAULT_INDEX = 1;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindViews({R.id.radio_button_play_list, R.id.radio_button_music_lib, R.id.radio_button_local_files, R.id.radio_button_settings})
    List<RadioButton> radioButtons;
    @BindView(R.id.radio_group_controls)
    RadioGroup mRadioGroup;
    String[] mTitles = {"播放列表", "音乐库", "本地音乐", "设置"};


    protected MediaPlayerService mPlayerService;
    private boolean isBoundService = false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mPlayerService = ((MediaPlayerService.LocalBinder) iBinder).getService();
            mPlayerService.registerCallBack(MainActivity.this);
            mPlayerService.setPlayMode(PlayMode.getCurrentMode());
            isBoundService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtil.i("onServiceDisconnected");
            mPlayerService = null;
            isBoundService = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        App.app.registerBindCallBack(this);
        mPlayerService = App.app.getPlayerService();
        if (mPlayerService != null) {
            mPlayerService.registerCallBack(MainActivity.this);
            mPlayerService.setPlayMode(PlayMode.getCurrentMode());
        }
        setSupportActionBar(mToolbar);
        initViewPagers();

    }

    private void initViewPagers() {
        final Fragment[] fragments = {new PlayListFragment(), new MusicFragment(), new LocalFragment(), new SettingFragment()};
        viewPager.setOffscreenPageLimit(fragments.length - 1);
        viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.mp_margin_large));
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Empty
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // Empty
            }

            @Override
            public void onPageSelected(int position) {
                radioButtons.get(position).setChecked(true);
            }
        });

        radioButtons.get(DEFAULT_INDEX).setChecked(true);
    }

    @OnCheckedChanged({R.id.radio_button_play_list, R.id.radio_button_music_lib, R.id.radio_button_local_files, R.id.radio_button_settings})
    public void onRadioButtonChecked(RadioButton button, boolean isChecked) {
        if (isChecked) {
            onItemChecked(radioButtons.indexOf(button));
        }
    }

    private void onItemChecked(int position) {
        viewPager.setCurrentItem(position);
        mToolbar.setTitle(mTitles[position]);
    }


    @OnClick({R.id.iv_play_bar_play, R.id.iv_play_bar_next, R.id.fl_play_bar})
    void doClick(View view) {
        switch (view.getId()) {
            case R.id.iv_play_bar_play:
                if (mPlayerService != null) {
                    if (mPlayerService.isPlaying()) {
                        mPlayerService.pause();
                    } else {
                        mPlayerService.play();
                    }
                }
                break;
            case R.id.iv_play_bar_next:
                if (mPlayerService != null) {
                    mPlayerService.playNext();
                }
                break;
            case R.id.fl_play_bar:
                PlayingActivity.start(0, MainActivity.this);
                break;
        }
    }


    @Override
    public void updateBuffer(int percent) {

    }

    @Override
    public void onSwitchPrev(SongInfoBean.BitrateEntity song) {
        mTvPlayBarTitle.setText(song.title);
        mIvPlayBarPlay.setImageResource(R.drawable.ic_play_bar_btn_pause);
    }

    @Override
    public void onSwitchNext(SongInfoBean.BitrateEntity song) {
        mTvPlayBarTitle.setText(song.title);
        mIvPlayBarPlay.setImageResource(R.drawable.ic_play_bar_btn_pause);
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onPlayStateChanged(boolean isPlaying) {
        if (isPlaying) {
            mIvPlayBarPlay.setImageResource(R.drawable.ic_play_bar_btn_pause);
        } else {
            mIvPlayBarPlay.setImageResource(R.drawable.ic_play_bar_btn_play);
        }
    }

    @Override
    public void onStatePlay(SongInfoBean.BitrateEntity song) {
        mTvPlayBarTitle.setText(song.title);
        mIvPlayBarPlay.setImageResource(R.drawable.ic_play_bar_btn_pause);
    }

    @Override
    public void onStateStop(SongInfoBean.BitrateEntity song) {
        mIvPlayBarPlay.setImageResource(R.drawable.ic_play_bar_btn_play);
    }

    @Override
    public void onProgressUpdate(int progress) {

    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onServiceBind() {
        if (mPlayerService == null) {
            mPlayerService=App.app.getPlayerService();
            mPlayerService.registerCallBack(MainActivity.this);
            mPlayerService.setPlayMode(PlayMode.getCurrentMode());
        }
    }
}

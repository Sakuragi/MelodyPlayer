package com.jim.melodyplayer.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jim.melodyplayer.R;
import com.jim.melodyplayer.base.CommonAdapter;
import com.jim.melodyplayer.base.FM;
import com.jim.melodyplayer.player.AudioPlayer;
import com.jim.melodyplayer.ui.fragments.LocalFragment;
import com.jim.melodyplayer.ui.fragments.MusicFragment;
import com.jim.melodyplayer.ui.fragments.PlayListFragment;
import com.jim.melodyplayer.ui.fragments.SettingFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private CommonAdapter<FM> mAdapter;
    private ArrayList<FM> datas;
    private String testUrl = "https://video.ydlcdn.com/2018/01/25/786b3e1640569ac1379505fdb0f8d1a8.mp3";
    public static final int INIT_SEEK_BAR = 1;
    public static final int UPDATE_SEEK_BAR = 2;
    private final int DEFAULT_INDEX=1;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindViews({R.id.radio_button_play_list, R.id.radio_button_music_lib, R.id.radio_button_local_files, R.id.radio_button_settings})
    List<RadioButton> radioButtons;
    @BindView(R.id.radio_group_controls)
    RadioGroup mRadioGroup;
    String[] mTitles = {"播放列表", "音乐库", "本地音乐", "设置"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        initViewPagers();

    }

    private void initViewPagers() {
        final Fragment[] fragments = {new PlayListFragment(),new MusicFragment(),new LocalFragment(),new SettingFragment()};
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

}

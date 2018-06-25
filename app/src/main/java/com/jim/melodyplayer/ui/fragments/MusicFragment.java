package com.jim.melodyplayer.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jim.melodyplayer.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Jim on 2018/6/16.
 */
public class MusicFragment extends Fragment {

    @BindView(R.id.tabs)
    TabLayout mTabs;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music_lib, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        if (mViewpager != null) {
            initViewPager();
            mViewpager.setOffscreenPageLimit(2);
        }
        mTabs.setupWithViewPager(mViewpager);
        mTabs.setTabTextColors(R.color.black,R.color.black);
        return rootView;
    }

    private void initViewPager() {
        Adapter adapter = new Adapter(getChildFragmentManager());
        RecommendFragment recommendFragment = new RecommendFragment();
//        recommendFragment.setChanger(this);
        adapter.addFragment(recommendFragment, "推荐");
        adapter.addFragment(new AllPlaylistFragment(), "专辑");
        //  adapter.addFragment(new NetFragment(), "主播电台");
        adapter.addFragment(new RankingFragment(), "排行榜");

        mViewpager.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    static class Adapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragments;
        private List<String> mTitles;

        public Adapter(FragmentManager fm) {
            super(fm);
            mFragments=new ArrayList<>();
            mTitles=new ArrayList<>();
        }

        public void addFragment(Fragment fragment,String title){
            mFragments.add(fragment);
            mTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }

}

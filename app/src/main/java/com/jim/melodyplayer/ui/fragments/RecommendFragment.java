package com.jim.melodyplayer.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jim.melodyplayer.R;
import com.jim.melodyplayer.base.BaseRecyclerView;
import com.jim.melodyplayer.base.BaseViewHolder;
import com.jim.melodyplayer.base.CommonAdapter;
import com.jim.melodyplayer.model.Banner;
import com.jim.melodyplayer.ui.LoodView;
import com.jim.melodyplayer.ui.widget.LoopView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Jim on 2018/6/20.
 */
public class RecommendFragment extends Fragment {


    @BindView(R.id.base_rcv)
    BaseRecyclerView mBaseRcv;
    Unbinder unbinder;

    private LoodView mLoopView;
    private CommonAdapter mCommonAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initRecyclerView();
        return rootView;
    }

    private void initRecyclerView() {
        mCommonAdapter=new CommonAdapter(getActivity(),R.layout.item_play_list,null) {
            @Override
            public void convert(BaseViewHolder holder, int position, Object data, int dataState) {

            }
        };
        mBaseRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        initHeaderViews();
        mBaseRcv.setAdapter(mCommonAdapter);
    }

    private void initHeaderViews() {
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.item_recommand_guide,mBaseRcv,false);
        mLoopView=view.findViewById(R.id.loop_view);
        if(mLoopView != null) mLoopView.requestFocus();
        mLoopView.upDateUI(createBannerDatas());
        mCommonAdapter.addHeaderView(view);
    }

    private List<Banner> createBannerDatas() {
        List<Banner> banners=new ArrayList<>();
        for (int i=0;i<=6;i++){
            Banner banner=new Banner();
            banner.imag_url="http://img2.imgtn.bdimg.com/it/u=3588772980,2454248748&fm=27&gp=0.jpg";
            banners.add(banner);
        }
        return banners;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

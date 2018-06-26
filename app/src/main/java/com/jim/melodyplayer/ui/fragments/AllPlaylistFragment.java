package com.jim.melodyplayer.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jim.melodyplayer.R;
import com.jim.melodyplayer.base.BaseRecyclerView;
import com.jim.melodyplayer.base.BaseViewHolder;
import com.jim.melodyplayer.base.CommonAdapter;
import com.jim.melodyplayer.net.NetUtils;
import com.jim.melodyplayer.model.SongListBean;
import com.jim.melodyplayer.net.SongListRequest;
import com.jim.melodyplayer.ui.activities.PlayingActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Jim on 2018/6/20.
 */
public class AllPlaylistFragment extends Fragment {


    @BindView(R.id.base_rcv)
    BaseRecyclerView mBaseRcv;
    Unbinder unbinder;

    private CommonAdapter<SongListBean.SongListEntity> mCommonAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initRecyclerView();
        loadData();
        return rootView;
    }

    private void initRecyclerView() {
        mBaseRcv.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCommonAdapter=new CommonAdapter<SongListBean.SongListEntity>(getActivity(),R.layout.item_play_list,new ArrayList<SongListBean.SongListEntity>()) {
            @Override
            public void convert(BaseViewHolder holder, int position, final SongListBean.SongListEntity data, int dataState) {
                holder.setText(R.id.text_view_name,data.getTitle());
                holder.setText(R.id.text_view_info,data.getAuthor());
                holder.setImageWithUrl(R.id.image_view_album,data.getPic_small());
                holder.setOnClickListener(R.id.rl_list_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PlayingActivity.start(Integer.valueOf(data.getSong_id()),getActivity());
                    }
                });
            }
        };
        mBaseRcv.setAdapter(mCommonAdapter);
    }

    private void loadData(){
        SongListRequest request=new SongListRequest();
        request.setType("1");
        NetUtils.fetchSongList(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<SongListBean>() {
                    @Override
                    public void call(SongListBean songListBeanBaseResponse) {
                        mCommonAdapter.addDatas(songListBeanBaseResponse.getSong_list());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

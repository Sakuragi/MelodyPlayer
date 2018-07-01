package com.jim.melodyplayer.net;

import com.jim.melodyplayer.model.SongInfoBean;
import com.jim.melodyplayer.model.SongListBean;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Jim on 2018/6/25.
 */
public interface NetApi {

    @GET("restserver/ting")
    Observable<SongListBean> fetchSongList(@QueryMap Map<String,String> map);

    @GET("restserver/ting")
    Observable<SongInfoBean> fetchSong(@QueryMap Map<String,String> map);

}

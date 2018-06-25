package com.jim.melodyplayer.net;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Jim on 2018/6/25.
 */
public interface NetApi {

    @GET("/restserver/ting")
    Observable<BaseResponse<SongListBean>> fetchSongList(@Body RequestBody body);

}

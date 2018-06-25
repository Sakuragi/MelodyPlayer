package com.jim.melodyplayer.net;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by Jim on 2018/6/25.
 */
public class NetUtils {

    public static Retrofit sRetrofit;
    public static OkHttpClient sOkHttpClient;
    private static final int READ_TIME_OUT=15;
    private static final int WRITE_TIME_OUT=15;
    private static final int CONNECT_TIME_OUT=15;

    public static Retrofit getRetrofit(){
        if (sRetrofit==null){
            sRetrofit=new Retrofit.Builder()
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("http://tingapi.ting.baidu.com/v1")
                    .client(getOkHttpClient())
                    .build();
        }
        return sRetrofit;
    }

    private static OkHttpClient getOkHttpClient(){
        if (sOkHttpClient==null){
            sOkHttpClient=new OkHttpClient.Builder()
                    .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIME_OUT,TimeUnit.SECONDS)
                    .connectTimeout(CONNECT_TIME_OUT,TimeUnit.SECONDS)
                    .build();
        }
        return sOkHttpClient;
    }

    private static RequestBody getRequestBody(Object obj) {
        String route = new Gson().toJson(obj);
        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),route);
        return body;
    }

    public static Observable<BaseResponse<SongListBean>> fetchSongList(SongListRequest request){
        return getRetrofit().create(NetApi.class).fetchSongList(getRequestBody(request));
    }

}

package com.jim.melodyplayer.net;

import com.jim.melodyplayer.model.SonInfoBean;
import com.jim.melodyplayer.model.SongListBean;
import com.jim.melodyplayer.utils.LogUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
                    .baseUrl("http://tingapi.ting.baidu.com/v1/")
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
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            LogUtil.d(chain.request().toString());
                            Request request=chain.request().newBuilder()
                                    .removeHeader("User-Agent")
                                    .addHeader("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)")
                                    .build() ;
                            return chain.proceed(request);
                        }
                    })
                    .build();
        }
        return sOkHttpClient;
    }

    private static Map<String, String> getRequestMap(Object obj) {
        Field[] fields=obj.getClass().getFields();
        HashMap<String,String> map=new HashMap<>();
        for (Field field:fields){
            try {
                map.put(field.getName(),field.get(obj)+"");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return map;
    }

    public static Observable<SongListBean> fetchSongList(SongListRequest request){
        return getRetrofit().create(NetApi.class).fetchSongList(getRequestMap(request));
    }

    public static Observable<SonInfoBean> fetchSong(SongRequest request){
        return getRetrofit().create(NetApi.class).fetchSong(getRequestMap(request));
    }

}

package com.jim.melodyplayer.app;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.danikula.videocache.HttpProxyCacheServer;
import com.jim.melodyplayer.player.MediaPlayerService;
import com.jim.melodyplayer.player.PlayMode;
import com.jim.melodyplayer.ui.activities.MainActivity;
import com.jim.melodyplayer.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by Jim on 2018/1/29 0029.
 */

public class App extends Application {

    public static App app;
    public MediaPlayerService mPlayerService;
    public ArrayList<ServiceBindCallBack> mCallBacks;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mPlayerService = ((MediaPlayerService.LocalBinder) iBinder).getService();
            for (ServiceBindCallBack callBack:mCallBacks){
                callBack.onServiceBind();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtil.i("onServiceDisconnected");
            mPlayerService = null;
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i("onCreate");
        mCallBacks=new ArrayList<>();
        app=this;
        bindService(new Intent(this,MediaPlayerService.class),mServiceConnection,Context.BIND_AUTO_CREATE);
    }

    public MediaPlayerService getPlayerService(){
        return mPlayerService;
    }

    public void registerBindCallBack(ServiceBindCallBack callBack){
        mCallBacks.add(callBack);
    }

    public interface ServiceBindCallBack{
        void onServiceBind();
    }

    public HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        App app = (App) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }

}

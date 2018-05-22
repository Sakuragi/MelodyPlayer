package com.jim.MelodyPlayer.app;

import android.app.Application;

/**
 * Created by Jim on 2018/1/29 0029.
 */

public class App extends Application {

    public static Application app;

    @Override
    public void onCreate() {
        super.onCreate();
        app=this;
    }
}

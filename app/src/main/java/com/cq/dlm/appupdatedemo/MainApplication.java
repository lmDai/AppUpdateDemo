package com.cq.dlm.appupdatedemo;

import android.app.Application;
import android.content.Context;


public class MainApplication extends Application {
    private static MainApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }

    public static Context getInstance() {
        return sInstance;
    }
}

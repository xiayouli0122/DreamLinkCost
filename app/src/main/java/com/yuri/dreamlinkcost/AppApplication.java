package com.yuri.dreamlinkcost;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by Yuri on 2015/7/7.
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //ActiveAndroid初始化
        ActiveAndroid.initialize(this);
    }
}

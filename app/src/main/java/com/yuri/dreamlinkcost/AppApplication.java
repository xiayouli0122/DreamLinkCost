package com.yuri.dreamlinkcost;

import android.app.Application;
import android.util.Log;

import com.activeandroid.ActiveAndroid;

/**
 * Created by Yuri on 2015/7/7.
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Yuri", "AppApplication");
        //ActiveAndroid初始化
        ActiveAndroid.initialize(this, true);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}

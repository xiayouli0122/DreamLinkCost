package com.yuri.dreamlinkcost;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.activeandroid.ActiveAndroid;
import com.bmob.BmobConfiguration;
import com.bmob.BmobPro;
import com.tencent.bugly.crashreport.CrashReport;
import com.yuri.xlog.Log;
import com.yuri.xlog.Settings;

import java.util.List;

/**
 * Created by Yuri on 2015/7/7.
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //避免调用多次onCreate()时，执行多次初始化

        //ActiveAndroid初始化
        ActiveAndroid.initialize(this, true);

        String processName = getProcessName(this, android.os.Process.myPid());
        if (processName != null) {
            boolean defaultProcess = processName.equals("com.yuri.dreamlinkcost");
            if (defaultProcess) {

                //Bugly
                CrashReport.initCrashReport(getApplicationContext(), "900005722", true);

                BmobConfiguration config = new BmobConfiguration.Builder(getApplicationContext())
                        .customExternalCacheDir(Environment.DIRECTORY_DOWNLOADS).build();
                BmobPro.getInstance(getApplicationContext()).initConfig(config);

//                LeakCanary.install(this);

                Log.initialize(Settings.getInstance()
                        .setAppTag("DreamLinkCost")
                        .isDebug(true));
            }
        }
    }

    /**
     * 返回进程名称
     */
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}

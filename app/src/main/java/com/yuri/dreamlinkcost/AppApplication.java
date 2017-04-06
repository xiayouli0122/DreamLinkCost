package com.yuri.dreamlinkcost;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.tencent.bugly.crashreport.CrashReport;
import com.yuri.xlog.Log;
import com.yuri.xlog.Settings;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;

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

                //默认初始化
//                Bmob.initialize(this, "da0e5e015563ce04e1675989c4d81012");
                //更详细初始化
                BmobConfig config = new BmobConfig.Builder(this)
                        //设置appkey
                        .setApplicationId(Constant.BMOB_APP_ID)
                        //请求超时时间（单位为秒）：默认15s
                        .setConnectTimeout(30)
                        //文件分片上传时每片的大小（单位字节），默认512*1024
                        .setUploadBlockSize(1024*1024)
                        //文件的过期时间(单位为秒)：默认1800s
                        .setFileExpiration(2400)
                        .build();
                Bmob.initialize(config);

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

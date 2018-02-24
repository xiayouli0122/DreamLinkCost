package com.yuri.dreamlinkcost

import android.app.ActivityManager
import android.app.Application
import android.content.Context

import com.activeandroid.ActiveAndroid
import com.tencent.bugly.crashreport.CrashReport
import com.yuri.xlog.Log
import com.yuri.xlog.Settings

import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobConfig

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //避免调用多次onCreate()时，执行多次初始化

        //ActiveAndroid初始化
        ActiveAndroid.initialize(this, true)

        val processName = getProcessName(this, android.os.Process.myPid())
        if (processName != null) {
            val defaultProcess = processName == "com.yuri.dreamlinkcost"
            if (defaultProcess) {

                //Bugly
                CrashReport.initCrashReport(applicationContext, "900005722", true)

                //默认初始化
                //                Bmob.initialize(this, "da0e5e015563ce04e1675989c4d81012");
                //更详细初始化
                val config = BmobConfig.Builder(this)
                        //设置appkey
                        .setApplicationId(Constant.BMOB_APP_ID)
                        //请求超时时间（单位为秒）：默认15s
                        .setConnectTimeout(30)
                        //文件分片上传时每片的大小（单位字节），默认512*1024
                        .setUploadBlockSize(1024 * 1024)
                        //文件的过期时间(单位为秒)：默认1800s
                        .setFileExpiration(2400)
                        .build()
                Bmob.initialize(config)

                //                LeakCanary.install(this);

                Log.initialize(Settings.getInstance()
                        .setAppTag("DreamLinkCost")
                        .isDebug(true))
            }
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        ActiveAndroid.dispose()
    }

    /**
     * 返回进程名称
     */
    private fun getProcessName(cxt: Context, pid: Int): String? {
        val am = cxt.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        return runningApps
                .firstOrNull { it.pid == pid }
                ?.processName
    }
}

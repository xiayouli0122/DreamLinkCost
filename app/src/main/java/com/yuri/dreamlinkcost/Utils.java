package com.yuri.dreamlinkcost;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.yuri.dreamlinkcost.log.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yuri on 2015/7/10.
 */
public class Utils {

    /**
     * 获取当前时间，并格式化
     * @return 当前时间格式化后的字符
     */
    public static String getDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(time);
        return format.format(date);
    }

    /**
     * 获取应用版本号
     * @param context
     * @return 当前版本号
     * @throws PackageManager.NameNotFoundException
     */
    public static String getAppVersion(Context context){
        String version = "";
        try{
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            version = packInfo.versionName;
        }catch(Exception e){
            Log.e(e.toString());
        }
        return version;
    }

    /**
     * 获取当前时间，并格式化
     * @return 当前时间格式化后的字符
     */
    public static String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }

    /**
     * 获取当前时间，并格式化
     * @return 当前时间格式化后的字符
     */
    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }

}

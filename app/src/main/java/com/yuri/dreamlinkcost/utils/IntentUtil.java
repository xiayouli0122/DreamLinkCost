package com.yuri.dreamlinkcost.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.Fragment;

import com.yuri.dreamlinkcost.utils.permission.PermissionCallback;
import com.yuri.dreamlinkcost.utils.permission.PermissionConstant;
import com.yuri.dreamlinkcost.utils.permission.PermissionManager;
import com.yuri.xlog.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yuri on 2016/5/19.
 */
public class IntentUtil {

    /**
     * 在activity中拨打电话，
     *
     * @param activity context对象，用于startActivity以及获取权限
     * @param tel      电话号码或者手机号码
     */
    public static void call(final Activity activity, final String tel) {
        //需要检查权限
        if (PermissionManager.hasPermission(activity, PermissionConstant.CALL_PHONE)) {
            doCall(activity, tel);
        } else {
            PermissionManager.askPermission(activity, PermissionConstant.CALL_PHONE, new PermissionCallback() {
                @Override
                public void onPermissionGranted() {
                    doCall(activity, tel);
                }

                @Override
                public void onPermissionRefused() {
                    Log.d();
                    ToastUtil.showToast(activity, "拨号权限被拒绝");
                }
            });
        }

    }

    /**
     * 拨打电话
     *
     * @param fragment context对象，用于startActivity以及获取权限
     * @param tel      电话号码或者手机号码
     */
    public static void call(final Fragment fragment, final String tel) {
        //需要检查权限
        if (PermissionManager.hasPermission(fragment.getContext(), PermissionConstant.CALL_PHONE)) {
            doCall(fragment.getContext(), tel);
        } else {
            PermissionManager.askPermission(fragment, PermissionConstant.CALL_PHONE, new PermissionCallback() {
                @Override
                public void onPermissionGranted() {
                    doCall(fragment.getContext(), tel);
                }

                @Override
                public void onPermissionRefused() {
                    Log.d();
                    ToastUtil.showToast(fragment.getContext(), "拨号权限被拒绝");
                }
            });
        }
    }

    /**
     * @param context
     * @param tel
     */
    public static void doCall(Context context, String tel) {
        Log.d(tel);
        try {
            context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tel)));
        } catch (SecurityException e) {
            Log.e(e.toString());
        }
    }

    /**
     * 跳转到app的应用信息界面
     */
    public static void goAppInfo(Context context, String packageName) {
        //跳转到应用信息界面
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.parse("package:" + packageName);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * 跳转到应用市场
     * @param context     context
     */
    public static void toAppMarket(Context context) {
        //跳转到已安装应用市场迪乐姆界面，如果该应用商店没有上架迪乐姆，将直接退出
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 跳转应用宝迪乐姆界面
     * @param context     context
     */
    public static void toTencentMarket(Context context) {
        //跳转到已安装应用市场迪乐姆界面，如果该应用商店没有上架迪乐姆，将直接退出
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.tencent.android.qqdownloader");
        context.startActivity(intent);
    }


    /**
     * 获取跳转到应用市场的Intent
     * @param context
     * @return Intent
     */
    public static Intent getMarketIntent(Context context) {
        StringBuilder localStringBuilder = new StringBuilder().append("market://details?id=");
        String str = context.getPackageName();
        localStringBuilder.append(str);
        Uri localUri = Uri.parse(localStringBuilder.toString());
        return new Intent(Intent.ACTION_VIEW, localUri);
    }

    public static boolean hasIntent(Context context, Intent intent) {
        List<ResolveInfo> localList = context.getPackageManager()
                .queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
        return localList != null && localList.size() > 0;
    }

    /**
     * 单发短信
     * @param context context
     * @param phone 目标手机号码
     */
    public static void sendMsgTo(Context context, String phone) {
        List<String> phones = new ArrayList<>();
        phones.add(phone);
        sendMsgTo(context, phones);
    }

    private static final String SMS_PRE = "smsto:";

    /**
     * 群发短信
     * @param context context
     * @param phones 手机号码列表，至少有一个手机号码
     */
    public static void sendMsgTo(Context context, List<String> phones) {
        String uriString = SMS_PRE;
        for (String phone : phones) {
            uriString += phone;
            uriString += ",";
        }
        if (uriString.equals("")) {
            return;
        }
        //去掉最后一个“,”
        uriString = uriString.substring(0, uriString.length() - 1);
        Log.e(uriString);
        Uri smsUri = Uri.parse(uriString);
        context.startActivity(new Intent( Intent.ACTION_SENDTO, smsUri));
    }

    /**
     * 当有新的媒体文件保存到sdcard的时候，通知系统更新media数据库
     * @param context context
     * @param path 媒体文件路径
     */
    public static void updateFileMedia(Context context, String path) {
        Uri data = Uri.parse("file://" + path);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
    }
}

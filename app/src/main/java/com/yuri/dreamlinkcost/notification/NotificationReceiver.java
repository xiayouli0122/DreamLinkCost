package com.yuri.dreamlinkcost.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.notification.pendingintent.ClickPendingIntentBroadCast;
import com.yuri.dreamlinkcost.utils.SharedPreferencesUtil;
import com.yuri.xlog.Log;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

/**
 * 监听通知栏被点击，请清除
 */
public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_NOTIFICATION_CLICK_INTENT = "com.dreamlink.cost.click.intent";
    public static final String ACTION_NOTIFICATION_VERSION_UPDATE = "com.dreamlink.cost.version.update";
    public static final String ACTION_NOTIFICATION_CANCEL = "com.dreamlink.cost.cancel";
    public static final String ACTION_NOTIFICATION_INSTALL_APP = "com.dreamlink.cost.install.app";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
        }
        if (ACTION_NOTIFICATION_CLICK_INTENT.equals(action)){
            Log.d("Yuri", "ACTION_NOTIFICATION_CLICK_INTENT");
            if (bundle != null) {
                String contentUrl = bundle.getString("contentUrl");
                int localPage = bundle.getInt("localPage");
                if (TextUtils.isEmpty(contentUrl)) {
                    launchLocalIntent(context, localPage);
                } else {
                    lanuchWebIntent(context, contentUrl);
                }
            }
        } else if (ACTION_NOTIFICATION_CANCEL.equals(action)) {
            if (bundle != null) {
                int id = bundle.getInt("id");
                Log.d("id:" + id);
                MMNotificationManager.getInstance(context).cancel(id);
            }
        } else if (ACTION_NOTIFICATION_VERSION_UPDATE.equals(action)) {
            Log.d("Download");
            String url = "";
            if (bundle != null) {
                url = bundle.getString("versionUrl");
            }
            MMNotificationManager.getInstance(context).cancel(Constant.NotificationID.VERSION_UPDAET);
//            lanuchWebIntent(context, url);
            downloadApk(context, url);
        } else if (ACTION_NOTIFICATION_INSTALL_APP.equals(action)) {
            Log.d("install APP");
            String path = SharedPreferencesUtil.get(context, "apkPath", null);
            Log.d("path:" + path);
            if (path != null) {
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installIntent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
                context.startActivity(installIntent);
            }
        }
    }

    private int mLastPercent = 0;
    private void downloadApk(final Context context, String fileName){
        Log.d(fileName);
        ClickPendingIntentBroadCast installApp = new ClickPendingIntentBroadCast(
                NotificationReceiver.ACTION_NOTIFICATION_INSTALL_APP);
        final NotificationBuilder builder = MMNotificationManager.getInstance(context).load();
        builder.setNotificationId(Constant.NotificationID.DOWNLOAD_APK);
        builder.setContentTitle("Apk下载中...");
        builder.setOnClickBroadCast(installApp);
        builder.setProgress(100, 0);
        builder.getSimpleNotification().build(false);
        final BmobFile bmobFile = new BmobFile("test.apk", "", fileName);
        bmobFile.download(new DownloadFileListener() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.d("path:" + s);
                    builder.setContentTitle("下载完成，点击按钮");
                    builder.getSimpleNotification().build(true);
                    SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHARED_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("apkPath", s);
                    editor.apply();

                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    installIntent.setDataAndType(Uri.parse("file://" + s), "application/vnd.android.package-archive");
                    context.startActivity(installIntent);
                } else {
                    Log.d("error:" + s);
                    builder.setContentTitle("下载失败:" + s);
                    builder.getSimpleNotification().build(true);
                }
            }

            @Override
            public void onProgress(Integer value, long newworkSpeed) {
                Log.d("path:" + bmobFile.getFileUrl() + ",progress:" + value);
                if (value != mLastPercent) {
                    mLastPercent = value;
                    builder.setProgress(100, value);
                    builder.getSimpleNotification().build(false);
                }
            }
        });
    }

    /**
     * 打开本地页面
     * @param localPage 本地页面标记
     */
    private void launchLocalIntent(Context context, int localPage){
        Log.d("Yuri", "localPage:" + localPage);
        Intent intent = new Intent();
        Bundle bundle = null;
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    /**
     * 打开网页
     * @param contentUrl 网页链接地址
     */
    private void lanuchWebIntent(Context context, String contentUrl){
        Log.d("Yuri", "contentUrl:" + contentUrl);
        if (!contentUrl.startsWith("http://")) {
            contentUrl = "http://" + contentUrl;
        }
        Uri uri = Uri.parse(contentUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

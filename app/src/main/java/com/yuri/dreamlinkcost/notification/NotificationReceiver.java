package com.yuri.dreamlinkcost.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.log.Log;

/**
 * 监听通知栏被点击，请清除
 */
public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_NOTIFICATION_CLICK_INTENT = "com.dreamlink.cost.click.intent";
    public static final String ACTION_NOTIFICATION_VERSION_UPDATE = "com.dreamlink.cost.version.update";
    public static final String ACTION_NOTIFICATION_CANCEL = "com.dreamlink.cost.cancel";

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
            lanuchWebIntent(context, url);
        }
    }

    /**
     * 打开本地页面
     * @param localPage 本地页面标记
     */
    private void launchLocalIntent(Context context, int localPage){
        Log.d("Yuri", "localPage:" + localPage);
        Intent intent = new Intent();
        Bundle bundle = null;
//        switch (localPage){
//            case LocalPage.ANNOUNCEMENT:
//                intent.setClass(context, AnnouncementActivity.class);
//                bundle = new Bundle();
//                bundle.putBoolean(AnnouncementActivity.EXTRA_IS_FROM_NOTIFICATION, true);
//                break;
//            case LocalPage.PROMOTION:
//                intent.setClass(context, MainActivity.class);
//                bundle = new Bundle();
//                bundle.putInt(MainActivity.JUMP_POSITION, LocalPage.PROMOTION);
//                break;
//            case LocalPage.REWARD:
//                intent.setClass(context, MainActivity.class);
//                bundle = new Bundle();
//                bundle.putInt(MainActivity.JUMP_POSITION, LocalPage.REWARD);
//                break;
//            default:
//                //默认跳转到主页
//                intent.setClass(context, MainActivity.class);
//                break;
//        }
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

package com.yuri.dreamlinkcost.notification.pendingintent;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.notification.MMNotificationManager;
import com.yuri.dreamlinkcost.notification.NotificationReceiver;
import com.yuri.dreamlinkcost.notification.interfaces.PendingIntentNotification;

/**用户点击通知后，发送一个广播*/
public class ClickPendingIntentBroadCast implements PendingIntentNotification {
    private Bundle mBundle;
    private int mRequestCode;
    private String action = NotificationReceiver.ACTION_NOTIFICATION_CLICK_INTENT;

    public ClickPendingIntentBroadCast(){
        mRequestCode = 0;
    }

    public ClickPendingIntentBroadCast(String action) {
        mRequestCode = 0;
        this.action = action;
    }

    public ClickPendingIntentBroadCast(Bundle bundle, int requestCode) {
        this.mBundle = bundle;
        this.mRequestCode = requestCode;
    }

    public void setBundle(Bundle bundle){
        this.mBundle = bundle;
    }

    public void setRequestCode(int requestCode){
        this.mRequestCode = requestCode;
    }

    @Override
    public PendingIntent onSettingPendingIntent() {
        Log.d("action:" + action);
        Intent clickIntentBroadcast = new Intent(action);
        clickIntentBroadcast.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        clickIntentBroadcast.setPackage(MMNotificationManager.mSingleton.mContext.getPackageName());
        if (mBundle != null) {
            Log.d("result:" + mBundle.getInt("result"));
            clickIntentBroadcast.putExtras(mBundle);
        }
        return PendingIntent.getBroadcast(MMNotificationManager.mSingleton.mContext, mRequestCode, clickIntentBroadcast,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

}

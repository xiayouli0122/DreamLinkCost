package com.yuri.dreamlinkcost.notification.pendingintent;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.yuri.dreamlinkcost.notification.MMNotificationManager;
import com.yuri.dreamlinkcost.notification.NotificationReceiver;
import com.yuri.dreamlinkcost.notification.interfaces.PendingIntentNotification;


/**用户点击后，打开一个Activity*/
public class ClickPendingIntentActivity implements PendingIntentNotification {
    private Class<?> mActivity;
    private Bundle mBundle;
    private int mRequestCode;

    public ClickPendingIntentActivity(){
        mRequestCode = 0;
    }

    public ClickPendingIntentActivity(Class<?> activity, Bundle bundle, int requestCode) {
        this.mActivity = activity;
        this.mBundle = bundle;
        this.mRequestCode = requestCode;
    }

    public void setClass(Class<?> activity){
        this.mActivity = activity;
    }

    public void setBundle(Bundle bundle){
        this.mBundle = bundle;
    }

    public void setRequestCode(int requestCode){
        this.mRequestCode = requestCode;
    }

    @Override
    public PendingIntent onSettingPendingIntent() {
        Log.d("Yuri", "");
        Intent clickIntentActivity = new Intent(MMNotificationManager.mSingleton.mContext, mActivity);
        clickIntentActivity.setAction(NotificationReceiver.ACTION_NOTIFICATION_CLICK_INTENT);
        clickIntentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        clickIntentActivity.setPackage(MMNotificationManager.mSingleton.mContext.getPackageName());

        if (mBundle != null) {
            Log.d("Yuri","mBundler is not null");
            clickIntentActivity.putExtras(mBundle);
        }
        return PendingIntent.getActivity(MMNotificationManager.mSingleton.mContext, mRequestCode, clickIntentActivity,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

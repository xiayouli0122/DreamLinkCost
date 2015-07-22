package com.yuri.dreamlinkcost.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;


public abstract class BasicNotification {
    protected final MMNotificationManager mMMNotification;
    protected String mTag;
    protected Notification mNotification;
    protected NotificationCompat.Builder mBuilder;
    protected int notificationId;
    
    protected String mTaskId;
    protected String mMessageId;

    public BasicNotification(NotificationCompat.Builder builder, int notificationId, String tag) {
        this.mMMNotification = MMNotificationManager.mSingleton;
        this.mBuilder = builder;
        this.notificationId = notificationId;
        this.mTag = tag;
    }

    public void build(boolean clear) {
        mNotification = mBuilder.build();
        if (!clear){
            mNotification.flags = Notification.FLAG_NO_CLEAR;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setBigContentView(RemoteViews views) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mNotification.bigContentView = views;
            return;
        }
        Log.w("Yuri", "Version does not support big content view");
    }

    protected Notification notificationNotify() {
        Log.d("Yuri","taskId:" + mTaskId + ",messageId:" + mMessageId);
        if(mTag != null){
            return notificationNotify(mTag, notificationId);
        }
        return notificationNotify(notificationId);
    }

    protected Notification notificationNotify(int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mMMNotification.mContext);
        notificationManager.notify(notificationId, mNotification);
        return mNotification;
    }

    protected Notification notificationNotify(String tag, int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mMMNotification.mContext);
        notificationManager.notify(tag, notificationId, mNotification);
        return mNotification;
    }
    
    protected void setTask(String taskId, String messageId) {
        mTaskId = taskId;
        mMessageId = messageId;
    }

}

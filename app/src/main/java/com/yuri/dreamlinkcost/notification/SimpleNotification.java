package com.yuri.dreamlinkcost.notification;

import android.app.Notification;
import android.support.v4.app.NotificationCompat.Builder;

public class SimpleNotification extends BasicNotification {
    private int mProgress;
    private int mMax;
    private boolean mIndeterminate;

    public SimpleNotification(Builder builder, int notificationId, String tag) {
        super(builder, notificationId, tag);
    }

    @Override
    public void build(boolean clear) {
        mBuilder.setProgress(mMax, mProgress, mIndeterminate);
        super.build(clear);
        super.notificationNotify();
    }

    public SimpleNotification update(int notificationId) {
        Builder builder = new Builder(MMNotificationManager.mSingleton.mContext);
        builder.setProgress(mMax, mProgress, mIndeterminate);

        mNotification = builder.build();
        mNotification.flags |= Notification.FLAG_NO_CLEAR;
        notificationNotify(notificationId);
        return this;
    }

    public SimpleNotification setProgress(int progress, int max, boolean indeterminate) {
        this.mProgress = progress;
        this.mMax = max;
        this.mIndeterminate = indeterminate;
        return this;
    }

    /**
     * 设置个推消息的taskId和messageId，便于通知展示时反馈给个推服务器
     * @param taskId 个推消息到达时获取
     * @param messageId 个推消息到达时获取
     */
    protected SimpleNotification setMessageTask(String taskId, String messageId) {
        super.setTask(taskId, messageId);
        return this;
    }
    
}

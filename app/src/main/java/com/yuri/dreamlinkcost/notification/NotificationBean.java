package com.yuri.dreamlinkcost.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

/**
 * 推送通知信息内容集合
 */
public class NotificationBean {
    /**通知ID*/
    public int mNotificationId;
    /**通知标题*/
    public String messageTitle;
    /**通知内容*/
    public String messageContent;
    
    /**Andriod 4.1通知栏支持bigContentView,暂时没用到*/
    public String mBigText;
    public String mSummaryText;
    /**状态栏显示的文字*/
    public String ticker;
    /**点击通知栏跳转页面标志*/
    public int localPage;
    /**富媒体图片*/
    public String mImageUrl;
    /**用户点击通知栏跳转web页面地址*/
    public String contentUrl;
    /**是否可以清除*/
    public boolean clear;
    /**显示通知时是否有声音*/
    public boolean sound;
    /**显示通知时是否震动*/
    public boolean vibrate;
    public PendingIntent clickPendingIntent;
    public PendingIntent deletePendingIntent;

    /**个推消息taskId*/
    public String mTaskId;
    /**个推消息messageId*/
    public String mMessageId;

    public NotificationBean(){
        this.mNotificationId = MMNotificationManager.radom();
        //默认是没有有声音和震动的
        sound = false;
        vibrate = false;
        clear = true;//默认可清除
    }

    public NotificationBean(int mNotificationId) {
        this.mNotificationId = mNotificationId;
        //默认是有声音和震动的
        sound = false;
        vibrate = false;
        clear = true;//默认可清除
    }

    @Override
    public String toString() {
        String string = "id:" + mNotificationId + ",messageTitle:" + messageTitle + ",messageContent:" + messageContent
                + ",ticker:" + ticker + ",localPage:" + localPage + ",contentUrl:" + contentUrl
                + ",clear:" + clear + ",sound:" + sound + ",vibrate:" + vibrate;
        return string;
    }

    /**创建一般的Notification*/
    public void buildSimple(Context context){
        Log.d("Yuri", "buildSimple");
        NotificationBuilder mBuilder = MMNotificationManager.getInstance(context).load()
                .setNotificationId(mNotificationId)
                .setContentTitle(messageTitle)
                .setContentText(messageContent)
                .setTicker(ticker)
                .setDefault(sound, vibrate);
        
        if (!TextUtils.isEmpty(mBigText)) {
            mBuilder.setBigTextStyle(mBigText, mSummaryText);
        }

        if (clickPendingIntent != null) {
            mBuilder.setOnClick(clickPendingIntent);
        }
        mBuilder.getSimpleNotification()
                .setMessageTask(mTaskId, mMessageId)
                .build(clear);
    }



    /**获取包含taskId和messageId的bundle对象*/
    private Bundle getClickBundle(){
        Bundle bundle = new Bundle();
        bundle.putString("contentUrl", contentUrl);
        bundle.putInt("localPage", localPage);
        return bundle;
    }
    
    public static void sendDemoNotification(Context context){
        NotificationBean notificationBean = new NotificationBean();
        notificationBean.messageTitle = "Notification Title";
        notificationBean.messageContent = "Notification Content";
        notificationBean.clear = true;
        notificationBean.contentUrl = "baidu.com";
        notificationBean.localPage = 1;
        notificationBean.buildSimple(context);
    }

}

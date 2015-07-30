package com.yuri.dreamlinkcost.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.text.Spanned;
import android.text.TextUtils;

import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.notification.interfaces.PendingIntentNotification;
import com.yuri.dreamlinkcost.notification.pendingintent.ClickPendingIntentActivity;
import com.yuri.dreamlinkcost.notification.pendingintent.ClickPendingIntentBroadCast;

/**
 * 构建通知内容
 * @author Yuri
 *
 */
public class NotificationBuilder {
    private final MMNotificationManager mNotification;
    private NotificationCompat.Builder mBuilder;
    private int mNotificationId;
    private String mTitle;
    private String mMessage;
    private int mSmallIconResId;
    private Spanned mMessageSpanned;
    private String mTag;

    public NotificationBuilder(MMNotificationManager notification) {
        this.mNotification = notification;
        this.mBuilder = new NotificationCompat.Builder(mNotification.mContext);
        this.createNotifationDefault();
    }

    private void createNotifationDefault() {
        this.mNotificationId = MMNotificationManager.radom();
        this.mBuilder.setContentTitle("");
        this.mBuilder.setContentText("");
        this.mBuilder.setAutoCancel(true);
        this.mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        this.mBuilder.setLargeIcon(BitmapFactory.decodeResource(mNotification.mContext.getResources(),
                R.mipmap.ic_launcher));
        this.mBuilder.setContentIntent(PendingIntent.getBroadcast(mNotification.mContext, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
    }
    
    /**
     * 设置默认声音和震动属性
     * @param hasSound 通知是否声音提示
     * @param hasVibrate 通知是否震动提示
     */
    public NotificationBuilder setDefault(boolean hasSound, boolean hasVibrate){
        if (hasSound && hasVibrate) {
            this.mBuilder.setDefaults(Notification.DEFAULT_ALL);
        } else if (hasSound && !hasVibrate) {
            this.mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
        } else if (!hasSound && hasVibrate) {
            this.mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);
        }
        return this;
    }

    /**
     * 设定Notification Id
     * @param notificationId id
     * @return {@link #NotificationBuilder}
     * @throws throws {@link IllegalStateException}
     */
    public NotificationBuilder setNotificationId(int notificationId) {
        if (notificationId <= 0) {
            this.mNotificationId = MMNotificationManager.radom();
            return this;
        }

        this.mNotificationId = notificationId;
        return this;
    }

    public NotificationBuilder setTag(@NonNull String tag) {
        this.mTag = tag;
        return this;
    }

    /**
     * 设置通知标题
     */
    public NotificationBuilder setContentTitle(@StringRes int title) {
        if (title <= 0) {
            return this;
        }
        this.mTitle = mNotification.mContext.getResources().getString(title);
        this.mBuilder.setContentTitle(this.mTitle);
        return this;
    }

    /**
     * 设置通知标题
     */
    public NotificationBuilder setContentTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return this;
        }
        this.mTitle = title;
        this.mBuilder.setContentTitle(this.mTitle);
        return this;
    }

    /**设置通知内容*/
    public NotificationBuilder setContentText(@StringRes int message) {
        if (message <= 0) {
            return this;
        }

        this.mMessage = mNotification.mContext.getResources().getString(message);
        this.mBuilder.setContentText(this.mMessage);
        return this;
    }

    /**设置通知内容*/
    public NotificationBuilder setContentText(@NonNull String message) {
        if (message.trim().length() == 0) {
            return this;
        }

        this.mMessage = message;
        this.mBuilder.setContentText(message);
        return this;
    }

    /**设置通知内容*/
    public NotificationBuilder setContentText(@NonNull Spanned message) {
        if (message.length() == 0) {
            return this;
        }

        this.mMessageSpanned = message;
        this.mBuilder.setContentText(message);
        return this;
    }

    /**Android API5.0新增 在图标图片后面的圆圈中设置一种强调色彩*/
    public NotificationBuilder setColor(int color) {
        this.mBuilder.setColor(color);
        return this;
    }

    /**设置通知栏提示语*/
    public NotificationBuilder setTicker(@StringRes int ticker) {
        this.mBuilder.setTicker(mNotification.mContext.getResources().getString(ticker));
        return this;
    }

    /**设置通知栏提示语*/
    public NotificationBuilder setTicker(String ticker) {
        if (TextUtils.isEmpty(ticker)){
            return this;
        }
        this.mBuilder.setTicker(ticker);
        return this;
    }

    /**设置通知展示的时间*/
    public NotificationBuilder setWhen(long when) {
        if (when <= 0) {
            return this;
        }
        this.mBuilder.setWhen(when);
        return this;
    }

    /**
     * 设置扩展文本通知阅读模式
     * @param bigTextResId
     * @param summaryTextResId
     */
    public NotificationBuilder setBigTextStyle(@StringRes int bigTextResId, @StringRes int summaryTextResId) {
        if (bigTextResId <= 0) {
            return this;
        }
        
        if (summaryTextResId <=0) {
            return setBigTextStyle(mNotification.mContext.getResources().getString(
                    bigTextResId), null);
        }
        
        return setBigTextStyle(mNotification.mContext.getResources().getString(
                bigTextResId), mNotification.mContext.getResources().getString(
                summaryTextResId));
    }

    /**
     * 设置扩展文本通知阅读模式
     * @param bigText
     * @param summaryText
     */
    public NotificationBuilder setBigTextStyle(@NonNull String bigText, String summaryText) {
        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText(bigText);
        if(summaryText != null){
            bigStyle.setSummaryText(summaryText);
        }
        this.mBuilder.setStyle(bigStyle);
        return this;
    }

    /**
     * 设置扩展文本通知阅读模式
     * @param bigTextStyle
     * @param summaryText
     */
    public NotificationBuilder setBigTextStyle(@NonNull Spanned bigTextStyle, String summaryText) {
        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText(bigTextStyle);
        if(summaryText != null){
            bigStyle.setSummaryText(summaryText);
        }
        this.mBuilder.setStyle(bigStyle);
        return this;
    }

    /**
     * 设置邮件收件箱模式通知
     * @param inboxLines
     * @param title
     * @param summary
     */
    public NotificationBuilder inboxStyle(@NonNull String[] inboxLines, @NonNull String title, String summary) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for (int i = 0; i < inboxLines.length; i++) {
            inboxStyle.addLine(inboxLines[i]);
        }
        inboxStyle.setBigContentTitle(title);
        if (summary != null) {
            inboxStyle.setSummaryText(summary);
        }
        this.mBuilder.setStyle(inboxStyle);
        return this;
    }

    /**
     * 设置用户点击通知后是否自动取消通知
     * @param autoCancel true,自动取消，否则不是
     */
    public NotificationBuilder setAutoCancel(boolean autoCancel) {
        this.mBuilder.setAutoCancel(autoCancel);
        return this;
    }

    /**
     * 设置通知展示小图标
     */
    public NotificationBuilder setSmallIcon(@DrawableRes int smallIcon) {
        this.mSmallIconResId = smallIcon;
        this.mBuilder.setSmallIcon(this.mSmallIconResId);
        return this;
    }

    /**
     * 设置通知展示大图标
     */
    public NotificationBuilder setLargeIcon(Bitmap bitmap) {
        this.mBuilder.setLargeIcon(bitmap);
        return this;
    }

    /**
     * 设置通知展示大图标
     */
    public NotificationBuilder setLargeIcon(@DrawableRes int largeIcon) {
        Bitmap bitmap = BitmapFactory.decodeResource(mNotification.mContext.getResources(), largeIcon);
        this.mBuilder.setLargeIcon(bitmap);
        return this;
    }

    /**
     * 通知可以设置组？ <br>
     * 更多请看官方文档：http://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#setGroup(java.lang.String)
     */
    public NotificationBuilder setGroup(@NonNull String groupKey){
        this.mBuilder.setGroup(groupKey);
        return this;
    }

    public NotificationBuilder setGroupSummary(boolean groupSummary){
        this.mBuilder.setGroupSummary(groupSummary);
        return this;
    }

    /**
     * 在通知的右侧可以显示一个数字
     * @param number 需要显示的数字
     */
    public NotificationBuilder setNumber(int number){
        this.mBuilder.setNumber(number);
        return this;
    }

    /**
     * 设置自定义通知提示声音 
     */
    public NotificationBuilder setSound(Uri sound) {
        this.mBuilder.setSound(sound);
        return this;
    }
    

    /**
     * 发起Notification后,铃声和震动均只执行一次 <br>
     * 官方文档解释：Set this flag if you would only like the sound, vibrate and ticker to be played if the notification is not already showing.
     * @param onlyAlertOnce
     * @return
     */
    public NotificationBuilder setOnlyAlertOnce(boolean onlyAlertOnce){
        this.mBuilder.setOnlyAlertOnce(onlyAlertOnce);
        return this;
    }

    /**???*/
    public NotificationBuilder addPerson(@NonNull String uri){
        if(uri.length() == 0){
            return this;
        }
        this.mBuilder.addPerson(uri);
        return this;
    }

    /**
     * 通知可以自定义按钮和点击事件（仅支持Android4.1以后,Android 4.1之前不会显示）
     * @param icon 按钮图标
     * @param title 按钮文字
     * @param pendingIntent 点击跳转事件
     */
    public NotificationBuilder addAction(@DrawableRes int icon, String title, PendingIntent pendingIntent) {
        this.mBuilder.addAction(icon, title, pendingIntent);
        return this;
    }

    /**
     * 通知可以自定义按钮和点击事件（仅支持Android4.1以后,Android 4.1之前不会显示）
     * @param icon 按钮图标
     * @param title 按钮文字
     */
    public NotificationBuilder addAction(@DrawableRes int icon, String title, PendingIntentNotification pendingIntentNotification) {
        this.mBuilder.addAction(icon, title, pendingIntentNotification.onSettingPendingIntent());
        return this;
    }

    public NotificationBuilder addAction(NotificationCompat.Action action) {
        this.mBuilder.addAction(action);
        return this;
    }

    public NotificationBuilder setProfit(int priority) {
        this.mBuilder.setPriority(priority);
        return this;
    }

    public NotificationBuilder setProgress(int max, int progress) {
        this.mBuilder.setProgress(max, progress, false);
        return this;
    }
    
    /**
     * 设置通知点击跳转事件
     */
    public NotificationBuilder setOnClick(Class<?> activity) {
        setOnClick(activity, null);
        return this;
    }

    /**
     * 设置通知点击跳转事件
     */
    public NotificationBuilder setOnClick(Class<?> activity, Bundle bundle) {
        this.mBuilder.setContentIntent(new ClickPendingIntentActivity(activity, bundle, mNotificationId).onSettingPendingIntent());
        return this;
    }

    /**
     * 设置通知点击发起广播
     */
    public NotificationBuilder setOnClickBroadCast(Bundle bundle) {
        ClickPendingIntentBroadCast broadCast;
        if (bundle == null) {
            broadCast = new ClickPendingIntentBroadCast();
        } else {
            broadCast = new ClickPendingIntentBroadCast(bundle, mNotificationId);
        }
        this.mBuilder.setContentIntent(broadCast.onSettingPendingIntent());
        return this;
    }

    public NotificationBuilder setOnClickBroadCast(PendingIntentNotification intent) {
        this.mBuilder.setContentIntent(intent.onSettingPendingIntent());
        return  this;
    }

    /**
     * 设置通知点击跳转事件
     * @param pendingIntent 自定义点击事件
     */
    public NotificationBuilder setOnClick(@NonNull PendingIntent pendingIntent) {
        this.mBuilder.setContentIntent(pendingIntent);
        return this;
    }

    /**
     * 设置通知被用户清除后的处理事件
     */
    public NotificationBuilder delete(@NonNull PendingIntent pendingIntent) {
        this.mBuilder.setDeleteIntent(pendingIntent);
        return this;
    }

    /**
     * 当在紧急情况下需要显示通知的时候，比如来电，闹钟，可以使用该方法，详细见官方文档
     */
    public void setFullScreenIntent(PendingIntent intent, boolean hightPorixty){
        this.mBuilder.setFullScreenIntent(intent, hightPorixty);
    }


    public SimpleNotification getSimpleNotification() {
        return new SimpleNotification(mBuilder, mNotificationId, mTag);
    }

}

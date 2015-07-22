package com.yuri.dreamlinkcost.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.util.Log;

import java.util.Random;

/**
 * 自定义通知类
 */
public class MMNotificationManager {
    public static MMNotificationManager mSingleton = null;
    public final Context mContext;

    private MMNotificationManager(Context context) {
        this.mContext = context;
    }

    public static MMNotificationManager getInstance(Context context) {
        if (mSingleton == null) {
            synchronized (MMNotificationManager.class) {
                if (mSingleton == null) {
                    mSingleton = new Contractor(context).build();
                }
            }
        }
        return mSingleton;
    }

    public NotificationBuilder load() {
        return new NotificationBuilder(this);
    }

    public void cancel(int notificationId) {
        try {
            checkMain();
        } catch (IllegalStateException e) {
            Log.e("Yuri", "Cancel:" + e.toString());
            return;
        }
        
        NotificationManager notifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.cancel(notificationId);
    }

    private static class Contractor {
        private final Context mContext;

        public Contractor(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null.");
            }
            this.mContext = context.getApplicationContext();
        }

        public MMNotificationManager build() {
            return new MMNotificationManager(mContext);
        }
    }

    /**
     * @throws IllegalStateException
     */
    public static void checkMain() throws IllegalStateException {
        if (!isMain()) {
            throw new IllegalStateException("Method call should happen from the main thread.");
        }
    }

    public static boolean isMain() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static int radom() {
        Random random = new Random();
        return random.nextInt(99999);
    }
    
    /**
     * 这是一个使用自定义通知类的例子o
     */
    public static void showNotificationDemo(Context context){
        //该Inetnt点击启动自身，如果已启动，直接调用到前台，未启动则启动之
        PackageManager pManager = context.getPackageManager();
        Intent appIntent = pManager.getLaunchIntentForPackage(context.getPackageName());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, 0);
        
        NotificationBuilder mBuilder = MMNotificationManager.getInstance(context).load()
                .setContentTitle("测试通知标题")
                .setContentText("测试通知内容")
                .setTicker("测试通知Ticker");

        mBuilder.setOnClick(pendingIntent);
        mBuilder.getSimpleNotification()
                .build(true);
    }
}

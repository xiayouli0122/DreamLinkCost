package com.yuri.dreamlinkcost.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Activity启动封装
 * Created by Yuri on 2016/5/19.
 */
public class ActivityUtil {

    /**
     * 最简单的启动activity的方式
     * @param context context
     * @param cls 需要启动的activity
     */
    public static void start(Context context, Class<?> cls) {
        context.startActivity(new Intent(context, cls));
    }

    /**
     * 有参数的启动activity的方式
     * @param context packageContext
     * @param cls 目标activity
     * @param bundle 需要传递的参数
     */
    public static void start(Context context, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * 最简单的启动activity的方式（有返回的）
     * @param activity activity对象，只有activity对象才能startActivityForResult
     * @param cls 需要启动的activity
     * @param requestCode result回调的值，需大于0
     */
    public static void startResult(Activity activity, Class<?> cls, int requestCode) {
        activity.startActivityForResult(new Intent(activity, cls), requestCode);
    }

    /**
     * 有参数的启动activity的方式（有返回的）
     * @param activity activity对象，只有activity对象才能startActivityForResult
     * @param cls 需要启动的activity
     * @param bundle 需要传递的参数
     * @param requestCode result回调的值，需大于0
     */
    public static void startResult(Activity activity, Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 最简单的启动activity的方式（有返回的）
     * @param fragment 从fragment启动
     * @param cls 需要启动的activity
     * @param requestCode result回调的值，需大于0
     */
    public static void startResult(Fragment fragment, Class<?> cls, int requestCode) {
        fragment.startActivityForResult(new Intent(fragment.getContext(), cls), requestCode);
    }

    /**
     * 有参数的启动activity的方式（有返回的）
     * @param fragment 从fragment启动
     * @param cls 需要启动的activity
     * @param bundle 需要传递的参数
     * @param requestCode result回调的值，需大于0
     */
    public static void startResult(Fragment fragment, Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        intent.setClass(fragment.getContext(), cls);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent, requestCode);
    }
}

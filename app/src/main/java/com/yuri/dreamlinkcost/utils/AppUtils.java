package com.yuri.dreamlinkcost.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

/**
 * 应用相关的公用方法
 */
public class AppUtils {
	
	/**SharedPrefenrence：记录登录用户的手机号码*/
	public static final String USERNAME_SP_KEY = "dilemu_userphone";
	
	/**
	 * @return 判断用户是否处于登录状态
	 */
	public static boolean isLogin(Context context){
		String phone = SharedPreferencesUtil.get(context, USERNAME_SP_KEY, "-1");
		return !phone.equals("-1");
	}

	public static void setLogout(Context context) {
		SharedPreferencesUtil.put(context, USERNAME_SP_KEY, "-1");
	}
	
	/**
	 * @param context
	 * @return 获取用户登录所用手机号码
	 */
	public static String getUserLoginPhoneNo(Context context){
		return SharedPreferencesUtil.get(context, USERNAME_SP_KEY, "");
	}
	
	/**
	 * @param context
	 * @return 删除用户登录所用的手机号码
	 */
	public static void removeUserPhone(Context context){
		SharedPreferencesUtil.remove(context, USERNAME_SP_KEY);
	}
	
	private static boolean isPackageInstalled(Context context, String packageName) {
		Intent queryIntent = new Intent();
		queryIntent.setPackage(packageName);
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> list = pm.queryIntentActivities(queryIntent, 0);
		return list != null && !list.isEmpty();
	}
	

    /**
     * 根据包名确认是否安装“微信”
     */
    public static boolean isWeiXinInstalled(Context context) {
        return isPackageInstalled(context, "com.tencent.mm");
    }

    /**
     * 根据包名确认是否安装“QQ”
     */
    public static boolean isQQInstalled(Context context) {
        return isPackageInstalled(context, "com.tencent.mobileqq");
    }

	/**
	 * 根据包名确认是否安装“微博”
	 */
	public static boolean isWeiBoInstalled(Context context) {
		return isPackageInstalled(context, "com.sina.weibo");
	}

	/**
	 * 根据包名确认是否安装“百度地图”
	 */
	public static boolean isBaiduMapInstalled(Context context) {
		return isPackageInstalled(context, "com.baidu.BaiduMap");
	}

	/**
	 * 确认是否安装应用宝
     */
	public static boolean isTencentMarketInstalled(Context context) {
		return isPackageInstalled(context, "com.tencent.android.qqdownloader");
	}
}

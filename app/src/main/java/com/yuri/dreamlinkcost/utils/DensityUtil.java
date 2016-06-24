package com.yuri.dreamlinkcost.utils;

import android.content.Context;
import android.content.res.Resources;

import java.lang.reflect.Field;

/**
 * @author GavinKwok
 * 获取设备的高度和宽度，以及提供dp和px单位之间的转换
 */
public class DensityUtil {
	
	/**
	 * @return 屏幕高度px值
	 */
	public static float getHeightInPx(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * @return 屏幕宽度px值
	 */
	public static float getWidthInPx(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * @return 屏幕高度dp值
	 */
	public static int getHeightInDp(Context context) {
		float height = context.getResources().getDisplayMetrics().heightPixels;
		return px2dip(context, height);
	}

	/**
	 * @return 屏幕宽度dp值
	 */
	public static int getWidthInDp(Context context) {
		float height = context.getResources().getDisplayMetrics().heightPixels;
		return px2dip(context, height);
	}

	/**
	 * @param dpValue 需要转换的dp值
	 * @return 转换后的px值
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		//还可以通过如下代码获得
		//Resources.getSystem().getDisplayMetrics().density
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * @param pxValue 需要转换的px值
	 * @return 转换后的dp值
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/**
	 * @param pxValue 需要转换的px值
	 * @return 转换后的sp值
	 */
	public static int px2sp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * @param spValue 需要转换的sp值
	 * @return 转换后的px值
	 */
	public static int sp2px(Context context, float spValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (spValue * scale + 0.5f);
	}

	/**
	 * 利用反射获取状态栏的高度
	 * @param resources getResource()
	 * @return the height of status bar
	 */
	public static int getStatusBarHeight(Resources resources) {
		int height = 0;
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object o = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = (Integer) field.get(o);
			height = resources.getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return height;
	}
}

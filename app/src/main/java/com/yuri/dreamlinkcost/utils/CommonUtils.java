package com.yuri.dreamlinkcost.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 基础工具类
 */
public class CommonUtils {

    /**
     * 生成一个32位的UUID
     */
    public static String generateUUID() {
        String s = UUID.randomUUID().toString();
        //去掉“-”符号
        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
    }

    /**
     * 获取Manifest中配置的键值对
     *
     * @param context
     * @param key     键
     * @return 键对应的值
     */
    public static String getMetaData(Context context, String key) {
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            Object value = ai.metaData.get(key);
            if (value != null) {
                return value.toString();
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    /**
     * 获取当前版本渠道ID
     *
     * @param context
     * @return 当前版本渠道ID
     */
    public static String getCurrentChannel(Context context) {
        return getMetaData(context, "CHANAL");
    }

    /**
     * 获取设备型号
     *
     * @return 设备型号字符串
     */
    public static String getPhoneModel() {
        return android.os.Build.MODEL;
    }


    /**
     * 验证码是否合法的校验
     *
     * @param number
     * @return true 合法； false 不合法
     */
    private static final String CODE_REG = "^\\d{4}$";

    public static boolean isValideCode(String number) {
        Pattern p = Pattern.compile(CODE_REG);
        Matcher m = p.matcher(number);
        return m.find();
    }

    /**
     * 验证手机号码是否合法的正则表达式
     */
    private static final String PHONE_REG = "^1[3|4578][0-9]\\d{8}$";

    /**
     * 验证手机号是否合法
     *
     * @param number 需要验证的手机号码
     * @return true 手机号码合法；false手机号码不合法
     */
    public static boolean isValidPhoneNumber(String number) {
        Pattern p = Pattern.compile(PHONE_REG);
        Matcher m = p.matcher(number);
        return m.find();
    }


    /**
     * 确保作为方法参数的对象引用非空
     *
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }


    /**
     * 确保作为方法参数的对象引用非空
     * @param reference an object reference
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *     string using {@link String#valueOf(Object)}
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference, @Nullable Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    /**
     * Ensures the truth of an expression involving one or more parameters to the calling method.
     *
     * @param expression a boolean expression
     * @param errorMessage the exception message to use if the check fails; will be converted to a
     *     string using {@link String#valueOf(Object)}
     * @throws IllegalArgumentException if {@code expression} is false
     */
    public static void checkArgument(boolean expression, @Nullable Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

}

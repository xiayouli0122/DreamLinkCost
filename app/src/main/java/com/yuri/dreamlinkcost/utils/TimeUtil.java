package com.yuri.dreamlinkcost.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 *
 * 时间相关常用工具类
 * Created by Yuri on 2016/4/20.
 */
public class TimeUtil {

    /**
     * 获取当前时间，并格式化
     *
     * @return 当前时间格式化后的字符
     */
    public static String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.CHINA);
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }

    /**
     * 获取当前时间，并格式化
     *
     * @return 当前时间格式化后的字符
     */
    public static String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = new Date(System.currentTimeMillis());
        return format.format(date);
    }

    /**
     * 获取当前时间，并格式化
     *
     * @return 当前时间格式化后的字符
     */
    public static String getDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        Date date = new Date(time);
        return format.format(date);
    }

    /**
     * 按指定格式返回时间
     *
     * @return 当前时间格式化后的字符
     */
    public static String getDate(long time,String formats) {
        SimpleDateFormat format = new SimpleDateFormat(formats, Locale.CHINA);
        Date date = new Date(time);
        return format.format(date);
    }


    /**
     * 获取宝宝的岁数 格式：2岁8个月
     * @param birthdayTime 宝宝的出生日期
     */
    public static String getBabyAge(long birthdayTime) {
        Calendar birthday = Calendar.getInstance();
        birthday.setTimeInMillis(birthdayTime);

        Calendar now = Calendar.getInstance();

        int day = now.get(Calendar.DAY_OF_MONTH) - birthday.get(Calendar.DAY_OF_MONTH);
        int month = now.get(Calendar.MONTH) - birthday.get(Calendar.MONTH);
        int year = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);

        //按照减法原理，先day相减，不够向month借；然后month相减，不够向year借；最后year相减。
        if (day < 0) {
            month -= 1;
            now.add(Calendar.MONTH, -1);//得到上一个月，用来得到上个月的天数。
            day += now.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        if (month < 0) {
            month = (month + 12) % 12;
            year--;
        }

        String result;
        if (year < 0) {
            return null;
        }

        if (year == 0 && month == 0 && day == 0) {
            return "0天";
        }

        String monthStr = month > 0 ? month + "个月" : "";
        String dayStr = day > 0 ? day + "天" : "";

        if (year > 0) {
            result = year + "年" + month + "个月";
        } else {
            result = monthStr + dayStr;
        }

        return result;
    }

    /**
     * 判断两个时间是否为同一天
     * @param timeA time in mills
     * @param timeB time in mills
     * @return true 两个时间为同一天；false：非同一天
     */
    public static boolean isSameDay(long timeA, long timeB) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTimeInMillis(timeA);
        Calendar calDateB = Calendar.getInstance();
        calDateB.setTimeInMillis(timeB);
        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB
                .get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 判断指定时间是否就是今天的日期
     * @param time time in mills
     * @return true toady，else not
     */
    public static boolean isToady(long time) {
        return time  > 0 && isSameDay(time, System.currentTimeMillis());
    }
}

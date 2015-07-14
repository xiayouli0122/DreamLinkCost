package com.yuri.dreamlinkcost;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yuri on 2015/7/10.
 */
public class Utils {

    /**
     * 获取当前时间，并格式化
     * @return 当前时间格式化后的字符
     */
    public static String getDate(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(time);
        return format.format(date);
    }
}

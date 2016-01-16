package com.yuri.dreamlinkcost.model.impl;

import android.content.Context;

/**
 * Created by Yuri on 2016/1/16.
 */
public interface IBaseMain {

    /**获取当前用户id*/
    int getUserId(Context context);

    String[] getTitles(Context context);
}

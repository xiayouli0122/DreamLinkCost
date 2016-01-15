package com.yuri.dreamlinkcost.model;

import android.content.Context;

import com.yuri.dreamlinkcost.bean.table.Cost;

/**
 * 接口类
 * Created by Yuri on 2016/1/15.
 */
public interface IAddNew {

    /**获取当前用户id*/
    int getUserId(Context context);

    String[] getTitles(Context context);

    void saveNewTitle(Context context, String title);

    void commit(Context context, Cost cost, CommitResultListener listener);
}

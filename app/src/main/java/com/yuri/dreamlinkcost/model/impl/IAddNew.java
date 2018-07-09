package com.yuri.dreamlinkcost.model.impl;

import android.content.Context;

import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri;
import com.yuri.dreamlinkcost.model.CommitResultListener;

/**
 * 接口类
 * Created by Yuri on 2016/1/15.
 */
public interface IAddNew extends IBaseMain{

    void saveNewTitle(Context context, String title);

    void commit(Context context, BmobCostYuri cost, CommitResultListener listener);
}

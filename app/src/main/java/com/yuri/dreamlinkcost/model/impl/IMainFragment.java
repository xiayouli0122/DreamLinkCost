package com.yuri.dreamlinkcost.model.impl;

import android.content.Context;

import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri;
import com.yuri.dreamlinkcost.bean.table.Cost;
import com.yuri.dreamlinkcost.model.CommitResultListener;
import com.yuri.dreamlinkcost.model.OnDeleteItemListener;
import com.yuri.dreamlinkcost.model.SyncDataResultListener;

import java.util.List;

/**
 * Created by Yuri on 2016/1/16.
 */
public interface IMainFragment extends IBaseMain{
    void commitLocalData(Context context, CommitResultListener listener);
    void commit(Context context, long id, CommitResultListener listener);
    void commit(Context context, Cost cost, CommitResultListener listener);
    void commit(Context context, List<Cost> costList, CommitResultListener listener);
    void delete(Context context, BmobCostYuri bmobCost, OnDeleteItemListener listener);
    void delete(Context context, Cost cost, OnDeleteItemListener listener);
    void syncData(Context context, SyncDataResultListener listener);
}

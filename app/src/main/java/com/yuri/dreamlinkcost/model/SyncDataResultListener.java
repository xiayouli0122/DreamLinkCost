package com.yuri.dreamlinkcost.model;

import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri;
import com.yuri.dreamlinkcost.bean.table.Cost;

import java.util.List;

/**
 * Created by Yuri on 2016/1/16.
 */
public interface SyncDataResultListener {
    void onSuccess(List<BmobCostYuri> serverList, List<Cost> localList);
    void onUpdateMoney(String result);
    void onFail(String msg);
}

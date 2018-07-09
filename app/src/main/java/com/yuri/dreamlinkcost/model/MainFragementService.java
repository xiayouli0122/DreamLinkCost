package com.yuri.dreamlinkcost.model;

import android.content.Context;

import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri;
import com.yuri.dreamlinkcost.model.impl.IMainFragment;
import com.yuri.xlog.Log;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Yuri on 2016/1/16.
 */
public class MainFragementService extends BaseMain implements IMainFragment{

    @Override
    public void delete(Context context, BmobCostYuri bmobCost, final OnDeleteItemListener listener) {
        bmobCost.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    if (listener != null) {
                        listener.onDeleteSucess();
                    }
                } else {
                    if (listener != null) {
                        listener.onDeleteFail(e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void syncData(Context context, final SyncDataResultListener listener) {
        Log.d();
        BmobQuery<BmobCostYuri> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("clear", false);
        bmobQuery.order("-createdDate");//按日期倒序排序
        bmobQuery.setLimit(1000);
        bmobQuery.findObjects(new FindListener<BmobCostYuri>() {
            @Override
            public void done(List<BmobCostYuri> list, BmobException e) {
                if (e == null) {
                    Log.d("serverSize=" + list.size());
                    Collections.sort(list, BmobCostYuri.DATE_DESC_COMPARATOR);
                    if (listener != null) {
                        listener.onSuccess(list);
                    }
                    if (list.size() == 0) {
                        if (listener != null) {
                            listener.onUpdateMoney("");
                        }
                    } else {
                        //统计一下
                        float totalPay = 0;
                        for (BmobCostYuri cos : list) {
                            totalPay += cos.totalPay;
                        }

                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        StringBuilder sb = new StringBuilder();
                        sb.append("累计:¥" + decimalFormat.format(totalPay) + ",总共" + list.size() + "条记录");
                        if (listener != null) {
                            listener.onUpdateMoney(sb.toString());
                        }
                    }
                } else {
                    Log.d("onError.errorCode:" + e.getErrorCode() + ",errorMsg:" + e.getMessage());
                    if (listener != null) {
                        listener.onFail(e.getMessage());
                    }
                }
            }
        });
    }
}

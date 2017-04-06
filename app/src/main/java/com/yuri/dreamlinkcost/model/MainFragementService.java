package com.yuri.dreamlinkcost.model;

import android.content.Context;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.bean.Bmob.BmobCost;
import com.yuri.dreamlinkcost.bean.table.Cost;
import com.yuri.dreamlinkcost.model.impl.IMainFragment;
import com.yuri.xlog.Log;

import java.text.DecimalFormat;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Yuri on 2016/1/16.
 */
public class MainFragementService extends BaseMain implements IMainFragment{

    @Override
    public void commitLocalData(Context context, CommitResultListener listener) {
        List<Cost> costs = new Select().from(Cost.class).where("status=?", Constant.STATUS_COMMIT_FAILURE).execute();
        BmobCost bmobCost;
        for (final Cost cost : costs) {
            bmobCost = cost.getCostBean();
            bmobCost.save(new SaveListener<String>() {
                @Override
                public void done(String objectId, BmobException e) {
                    if (e == null) {
                        Log.d("Yuri", "upload success:" + cost.title);
                        //上传成功后，删除本地数据
                        cost.delete();
                    } else {
                        Log.d("Yuri", "upload failure:" + cost.title);
                    }
                }
            });
        }
    }

    @Override
    public void commit(final Context context, long id, final CommitResultListener listener) {
        final Cost cost = Cost.load(Cost.class, id);
        final BmobCost bmobCost = cost.getCostBean();
        bmobCost.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Log.d("upload success:" + cost.title);
                    Toast.makeText(context, "upload success", Toast.LENGTH_SHORT).show();
                    cost.status = Constant.STATUS_COMMIT_SUCCESS;
                    cost.objectId = bmobCost.getObjectId();
                    cost.save();

                    if (listener != null) {
                        listener.onCommitSuccess();
                    }
                } else {
                    if (listener != null) {
                        listener.onCommitFail(e.getErrorCode(), e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void commit(Context context, Cost cost, CommitResultListener listener) {

    }

    @Override
    public void commit(Context context, List<Cost> costList, CommitResultListener listener) {

    }

    @Override
    public void delete(Context context, BmobCost bmobCost, final OnDeleteItemListener listener) {
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
    public void delete(Context context, Cost cost, OnDeleteItemListener listener) {

    }

    @Override
    public void syncData(Context context, final SyncDataResultListener listener) {
        Log.d();
        BmobQuery<BmobCost> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("clear", false);
        bmobQuery.order("-createDate");//按日期倒序排序
        bmobQuery.setLimit(1000);
        bmobQuery.findObjects(new FindListener<BmobCost>() {
            @Override
            public void done(List<BmobCost> list, BmobException e) {
                if (e == null) {
                    Log.d("serverSize=" + list.size());
                    List<Cost> localList = new Select().from(Cost.class).where("clear=?", 0).orderBy("id desc").execute();
                    Log.d("localSize=" + localList.size());

                    if (listener != null) {
                        listener.onSuccess(list, localList);
                    }

                    if (list.size() + localList.size() == 0) {
                        if (listener != null) {
                            listener.onUpdateMoney("");
                        }
                    } else {
                        //统计一下
                        float liuchengPay = 0;
                        float xiaofeiPay = 0;
                        float yuriPay = 0;
                        for (BmobCost cos : list) {
                            liuchengPay += cos.payLC;
                            xiaofeiPay += cos.payXF;
                            yuriPay += cos.payYuri;
                        }

                        for (Cost cost : localList) {
                            liuchengPay += cost.payLC;
                            xiaofeiPay += cost.payXF;
                            yuriPay += cost.payYuri;
                        }

                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        StringBuilder sb = new StringBuilder();
                        sb.append("L:" + decimalFormat.format(liuchengPay) + ",");
                        sb.append("X:" + decimalFormat.format(xiaofeiPay) + ",");
                        sb.append("Y:" + decimalFormat.format(yuriPay));
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

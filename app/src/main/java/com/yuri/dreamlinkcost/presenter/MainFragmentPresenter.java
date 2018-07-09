package com.yuri.dreamlinkcost.presenter;

import android.content.Context;

import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri;
import com.yuri.dreamlinkcost.model.MainFragementService;
import com.yuri.dreamlinkcost.model.OnDeleteItemListener;
import com.yuri.dreamlinkcost.model.SyncDataResultListener;
import com.yuri.dreamlinkcost.model.impl.IMainFragment;
import com.yuri.dreamlinkcost.view.impl.IMainFragmentView;

import java.util.List;

/**
 * Created by Yuri on 2016/1/16.
 */
public class MainFragmentPresenter extends BasePresenter<IMainFragmentView> {

    private IMainFragment iMainFragment;
    public MainFragmentPresenter(Context context, IMainFragmentView view) {
        super(context, view);
        iMainFragment = new MainFragementService();
    }

    public void syncData() {
        iMainFragment.syncData(mContext, new SyncDataResultListener() {
            @Override
            public void onSuccess(List<BmobCostYuri> serverList) {
                mView.updateList(true, serverList);
            }

            @Override
            public void onUpdateMoney(String result) {
                mView.updateTitleMoney(result);
            }

            @Override
            public void onFail(String msg) {
                mView.updateList(false, null);
            }
        });
    }

    public void deleteItem(BmobCostYuri bmobCost, OnDeleteItemListener listener) {
        iMainFragment.delete(mContext, bmobCost, listener);
    }
}

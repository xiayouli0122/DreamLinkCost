package com.yuri.dreamlinkcost.presenter;

import android.content.Context;

import com.yuri.dreamlinkcost.view.impl.IBaseView;

/**
 * Created by Yuri on 2016/1/16.
 */
public class BasePresenter<T extends IBaseView> {

    protected T mView;
    protected Context mContext;

    public BasePresenter(Context mContext) {
        this.mContext = mContext;
    }

    public BasePresenter(Context context, T view) {
        mContext = context;
        mView = view;
    }
}

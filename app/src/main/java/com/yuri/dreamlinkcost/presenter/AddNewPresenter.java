package com.yuri.dreamlinkcost.presenter;

import android.content.Context;

import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri;
import com.yuri.dreamlinkcost.model.AddNew;
import com.yuri.dreamlinkcost.model.CommitResultListener;
import com.yuri.dreamlinkcost.model.impl.IAddNew;

/**
 * Created by Yuri on 2016/1/15.
 */
public class AddNewPresenter extends BasePresenter{

    private IAddNew iAddNew;

    public AddNewPresenter(Context context) {
        super(context);
        this.iAddNew = new AddNew();
    }

    public int getUserId() {
        return iAddNew.getUserId(mContext);
    }

    public String[] getTitles() {
        return iAddNew.getTitles(mContext);
    }

    public void saveTitle(String title) {
        iAddNew.saveNewTitle(mContext, title);
    }

    public void commit(final BmobCostYuri cost, CommitResultListener listener) {
        iAddNew.commit(mContext, cost, listener);
    }
}

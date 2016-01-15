package com.yuri.dreamlinkcost.presenter;

import android.content.Context;

import com.yuri.dreamlinkcost.bean.table.Cost;
import com.yuri.dreamlinkcost.model.AddNew;
import com.yuri.dreamlinkcost.model.CommitResultListener;
import com.yuri.dreamlinkcost.model.IAddNew;

/**
 * Created by Yuri on 2016/1/15.
 */
public class AddNewPresenter {

    private IAddNew iAddNew;
    private Context context;

    public AddNewPresenter(Context context) {
        this.context = context;
        this.iAddNew = new AddNew();
    }

    public int getUserId() {
        return iAddNew.getUserId(context);
    }

    public String[] getTitles() {
        return iAddNew.getTitles(context);
    }

    public void saveTitle(String title) {
        iAddNew.saveNewTitle(context, title);
    }

    public void commit(final Cost cost, CommitResultListener listener) {
        iAddNew.commit(context, cost, listener);
    }
}

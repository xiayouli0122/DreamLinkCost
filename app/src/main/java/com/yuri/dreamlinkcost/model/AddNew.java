package com.yuri.dreamlinkcost.model;

import android.content.Context;

import com.activeandroid.query.Select;
import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri;
import com.yuri.dreamlinkcost.bean.Bmob.BmobTitle;
import com.yuri.dreamlinkcost.bean.table.Title;
import com.yuri.dreamlinkcost.model.impl.IAddNew;
import com.yuri.xlog.Log;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 *
 * 实现类
 * Created by Yuri on 2016/1/15.
 */
public class AddNew extends BaseMain implements IAddNew {

    @Override
    public void saveNewTitle(Context context, String titleStr) {
        Title title = new Select().from(Title.class).where("title=?", titleStr).executeSingle();
        if (title == null) {
            title = new Title();
            title.mTitle = titleStr;
            title.mHasCommited = false;
            title.save();
            BmobTitle bmobTitle = title.getBmobTitle();
            final Title finalTitle = title;
            bmobTitle.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        finalTitle.mHasCommited = true;
                        finalTitle.save();
                    }
                }
            });
        }
    }

    @Override
    public void commit(final Context context, final BmobCostYuri cost, final CommitResultListener listener) {
        if (listener == null) {
            throw new NullPointerException("CommitResultListener cannot be null");
        }

        cost.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.d();
                    saveNewTitle(context, cost.title);
                    listener.onCommitSuccess();
                } else {
                    listener.onCommitFail(e.getErrorCode(), e.getMessage());
                }
            }
        });
    }

}

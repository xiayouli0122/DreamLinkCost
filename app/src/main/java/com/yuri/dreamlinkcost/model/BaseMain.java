package com.yuri.dreamlinkcost.model;

import android.content.Context;

import com.activeandroid.query.Select;
import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.SharedPreferencesManager;
import com.yuri.dreamlinkcost.bean.table.Title;
import com.yuri.dreamlinkcost.model.impl.IBaseMain;

import java.util.List;

/**
 * Created by Yuri on 2016/1/16.
 */
public class BaseMain implements IBaseMain {
    @Override
    public int getUserId(Context context) {
        return SharedPreferencesManager.get(context, Constant.Extra.KEY_LOGIN, Constant.Author.YURI);
    }

    @Override
    public String[] getTitles(Context context) {
        List<Title> titles = new Select().from(Title.class).execute();
        String[] titleArrays ;
        if (titles == null) {
            titleArrays = context.getResources().getStringArray(R.array.title_arrays);
        } else {
            titleArrays = new String[titles.size()];
            for (int i = 0; i < titles.size(); i++) {
                titleArrays[i] = titles.get(i).mTitle;
            }
        }
        return titleArrays;
    }
}

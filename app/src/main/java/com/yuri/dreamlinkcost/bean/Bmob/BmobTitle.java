package com.yuri.dreamlinkcost.bean.Bmob;

import com.yuri.dreamlinkcost.bean.table.Title;

import cn.bmob.v3.BmobObject;

/**
 * Created by Yuri on 2015/7/21.
 */
public class BmobTitle extends BmobObject{

    public String title;

    public Title getTitle() {
        Title title = new Title();
        title.mTitle = this.title;
        title.mObjectId = this.getObjectId();
        title.mHasCommited = true;
        return  title;
    }
}

package com.yuri.dreamlinkcost.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.yuri.dreamlinkcost.Bmob.BmobTitle;

/**
 * Created by Yuri on 2015/7/18.
 */
@Table(name = "Title")
public class Title extends Model {

    @Column(name = "title")
    public String mTitle;

    @Column(name = "objectId")
    public String mObjectId;

    @Column(name = "hasCommited")
    public boolean mHasCommited;

    public BmobTitle getBmobTitle() {
        if (mHasCommited) {
            return null;
        }

        BmobTitle bmobTitle = new BmobTitle();
        bmobTitle.title = this.mTitle;
        return bmobTitle;
    }
}

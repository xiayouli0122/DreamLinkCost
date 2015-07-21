package com.yuri.dreamlinkcost.Bmob;

import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.model.Cost;

import cn.bmob.v3.BmobObject;

/**
 * Created by Yuri on 2015/7/6.
 */
public class BmobCost extends BmobObject{

    public int author;

    public float totalPay;
    public String title;


    public float payLC;
    public float payXF;
    public float payYuri;

    public boolean clear;

    public long createDate;

    public Cost getCost() {
        Cost cost = new Cost();
        cost.author = this.author;
        cost.objectId = getObjectId();
        cost.status = Constant.STATUS_COMMIT_SUCCESS;
        cost.createDate = this.createDate;
        cost.payLC = this.payLC;
        cost.payXF = this.payXF;
        cost.payYuri = this.payYuri;
        cost.title = this.title;
        cost.totalPay = this.totalPay;
        cost.clear = this.clear;
        return cost;
    }

    @Override
    public String toString() {
        return "BmobCost{" +
                "author=" + author +
                ", totalPay=" + totalPay +
                ", title='" + title + '\'' +
                ", payLC=" + payLC +
                ", payXF=" + payXF +
                ", payYuri=" + payYuri +
                ", clear=" + clear +
                ", createDate=" + createDate +
                '}';
    }
}

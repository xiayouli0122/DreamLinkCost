package com.yuri.dreamlinkcost.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.yuri.dreamlinkcost.Bmob.BmobCost;
import com.yuri.dreamlinkcost.Constant;

/**
 * Created by Yuri on 2015/7/7.
 */
@Table(name = "cost")
public class Cost extends Model {

    @Column(name = "objectId")
    public String objectId;
    @Column(name = "totalPay")
    public float totalPay;
    @Column(name = "title")
    public String title;

    @Column(name = "payLC")
    public float payLC;
    @Column(name = "payXF")
    public float payXF;
    @Column(name = "payYuri")
    public float payYuri;

    @Column(name = "status")
    public int status = Constant.STATUS_COMMIT_FAILURE;

    @Column(name = "createDate")
    public long createDate;

    @Column(name = "author")
    public int author = Constant.Author.YURI;

    @Column(name = "clear")
    public boolean clear = false;

    @Override
    public String toString() {
        return "Cost{" +
                "objectId='" + objectId + '\'' +
                ", totalPay=" + totalPay +
                ", title='" + title + '\'' +
                ", payLC=" + payLC +
                ", payXF=" + payXF +
                ", payYuri=" + payYuri +
                ", status=" + status +
                ", createDate=" + createDate +
                ", author=" + author +
                ", clear=" + clear +
                '}';
    }

    public BmobCost getCostBean() {
        BmobCost costBean = new BmobCost();
        costBean.totalPay = this.totalPay;
        costBean.title = this.title;
        costBean.payLC = this.payLC;
        costBean.payXF = this.payXF;
        costBean.payYuri = this.payYuri;
        costBean.author = this.author;
        costBean.createDate = this.createDate;
        costBean.clear = this.clear;
        return costBean;
    }
}

package com.yuri.dreamlinkcost.Bmob;

import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.model.Cost;

import java.text.Collator;
import java.util.Comparator;

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

    /**
     * Perform alphabetical comparison of application entry objects.
     */
    public static final Comparator<BmobCost> DATE_COMPARATOR = new Comparator<BmobCost>() {
        private final Collator sCollator = Collator.getInstance();
        @Override
        public int compare(BmobCost object1, BmobCost object2) {
            if (object1.createDate > object2.createDate)
                return  -1;

            if (object1.createDate == object2.createDate)
                return 0;

            if (object1.createDate < object2.createDate)
                return 0;

            return 0;
        }
    };

    /**
     * Perform alphabetical comparison of application entry objects.
     */
    public static final Comparator<BmobCost> PRICE_COMPARATOR = new Comparator<BmobCost>() {
        private final Collator sCollator = Collator.getInstance();
        @Override
        public int compare(BmobCost object1, BmobCost object2) {
            if (object1.totalPay > object2.totalPay)
                return  -1;

            if (object1.totalPay == object2.totalPay)
                return 0;

            if (object1.totalPay < object2.totalPay)
                return 0;

            return 0;
        }
    };
}

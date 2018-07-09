package com.yuri.dreamlinkcost.bean.Bmob;

import java.util.Comparator;

import cn.bmob.v3.BmobObject;

/**
 * Created by Yuri on 2015/7/6.
 */
public class BmobCostYuri extends BmobObject{

    public int author;

    public float totalPay;
    public String title;

    public boolean clear;

    public long createDate;

    @Override
    public String toString() {
        return "BmobCost{" +
                "author=" + author +
                ", totalPay=" + totalPay +
                ", title='" + title + '\'' +
                ", clear=" + clear +
                ", createDate=" + createDate +
                '}';
    }

    /**
     * 按时间进行升序排列 如果 v1 > v2 返回1，如果v1 < v2 返回-1
     */
    public static final Comparator<BmobCostYuri> DATE_ASC_COMPARATOR = new Comparator<BmobCostYuri>() {
        @Override
        public int compare(BmobCostYuri object1, BmobCostYuri object2) {
            if (object1.createDate > object2.createDate)
                return  1;

            if (object1.createDate == object2.createDate)
                return 0;

            if (object1.createDate < object2.createDate)
                return -1;

            return 0;
        }
    };

    /**
     * 按时间进行倒序排列 如果 v1 > v2 返回-1，如果v1 < v2 返回1
     */
    public static final Comparator<BmobCostYuri> DATE_DESC_COMPARATOR = new Comparator<BmobCostYuri>() {
        @Override
        public int compare(BmobCostYuri object1, BmobCostYuri object2) {
            if (object1.createDate > object2.createDate)
                return  -1;

            if (object1.createDate == object2.createDate)
                return 0;

            if (object1.createDate < object2.createDate)
                return 1;

            return 0;
        }
    };

    /**
     * 价格升序.
     */
    public static final Comparator<BmobCostYuri> PRICE_ASC_COMPARATOR = new Comparator<BmobCostYuri>() {
        @Override
        public int compare(BmobCostYuri object1, BmobCostYuri object2) {
            if (object1.totalPay > object2.totalPay)
                return  1;

            if (object1.totalPay == object2.totalPay)
                return 0;

            if (object1.totalPay < object2.totalPay)
                return -1;

            return 0;
        }
    };

    /**
     * 价格倒序.
     */
    public static final Comparator<BmobCostYuri> PRICE_DESC_COMPARATOR = new Comparator<BmobCostYuri>() {
        @Override
        public int compare(BmobCostYuri object1, BmobCostYuri object2) {
            if (object1.totalPay > object2.totalPay)
                return  -1;

            if (object1.totalPay == object2.totalPay)
                return 0;

            if (object1.totalPay < object2.totalPay)
                return 1;

            return 0;
        }
    };
}

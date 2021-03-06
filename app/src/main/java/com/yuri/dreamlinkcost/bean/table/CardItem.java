package com.yuri.dreamlinkcost.bean.table;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri;
import com.yuri.dreamlinkcost.utils.TimeUtil;

import java.text.DecimalFormat;

public class CardItem {
    public String title;
    public String info;
    public boolean isCommited;
    public String date;
    public int header;

    private DecimalFormat mDecimalFormat;
    private Context mContext;
    public CardItem(Context context) {
        mDecimalFormat=new DecimalFormat("0.00");
        mContext = context;
    }

    public CardItem getCardItem(BmobCostYuri bmobCost) {
        this.title = bmobCost.title;
//        String detail = "L:" + (bmobCost.payLC == 0 ? bmobCost.payLC + "" : mDecimalFormat.format(bmobCost.payLC))
//                + ", X:" + (bmobCost.payXF == 0 ? bmobCost.payXF + "" : mDecimalFormat.format(bmobCost.payXF))
//                + ", Y:" + (bmobCost.payYuri == 0 ? bmobCost.payYuri + "" : mDecimalFormat.format(bmobCost.payYuri));
        this.info = "¥" + bmobCost.totalPay;
        this.isCommited = true;
        this.date = TimeUtil.getDate(bmobCost.createDate);
        this.header = Constant.Author.YURI;
//        if (bmobCost.payLC > 0) {
//            this.header = Constant.Author.LIUCHENG;
//        } else if (bmobCost.payXF > 0) {
//            this.header = Constant.Author.XIAOFEI;
//        } else {
//            this.header = Constant.Author.YURI;
//        }
        return this;
    }

    public static String getHeaderText(int header) {
        if (header == Constant.Author.LIUCHENG) {
            return "L";
        } else if (header == Constant.Author.XIAOFEI) {
            return "X";
        } else {
            return  "Y";
        }
    }

    /**直接可以在layout中调用*/
    public Drawable getItemBackgroudRes() {
        int resid = R.drawable.round_yuri;
        if (header == Constant.Author.LIUCHENG) {
            resid =  R.drawable.round_liucheng;
        } else if (header == Constant.Author.XIAOFEI) {
            resid =  R.drawable.round_xiaofei;
        }
        return mContext.getResources().getDrawable(resid);
    }
}

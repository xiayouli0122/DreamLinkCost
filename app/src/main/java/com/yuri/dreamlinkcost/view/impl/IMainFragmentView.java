package com.yuri.dreamlinkcost.view.impl;

import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri;

import java.util.List;

/**
 * Created by Yuri on 2016/1/16.
 */
public interface IMainFragmentView extends IBaseView {

    void updateList(boolean result, List<BmobCostYuri> serverList);
    void updateTitleMoney(String result);
}

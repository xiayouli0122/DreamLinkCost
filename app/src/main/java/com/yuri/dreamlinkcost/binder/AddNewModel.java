package com.yuri.dreamlinkcost.binder;

import android.databinding.Observable;
import android.databinding.ObservableField;
import android.view.View;

import com.yuri.dreamlinkcost.log.Log;

/**
 * Created by Yuri on 2015/9/17.
 */
public class AddNewModel {

    public String title;
    public String totalPrice;
    public String liucheng;
    public String xiaofei;
    public String yuri;

    //谁参加了
//    public boolean isLiuChengIn,isXiaoFeiIn,isYuriIn;
    public final ObservableField<Boolean> isLiuChengIn = new ObservableField<>();
    public final ObservableField<Boolean> isXiaoFeiIn = new ObservableField<>();
    public final ObservableField<Boolean> isYuriIn = new ObservableField<>();

//    //平摊是否被选中
    public final ObservableField<Boolean> isAverageUserChecked = new ObservableField<>();
//    //个人价格编辑框隐藏与现实
    public final ObservableField<Integer> itemPriceBlockVisiblity = new ObservableField<>();
    //付款人
    public final ObservableField<Integer> whichOnePay = new ObservableField<>();
    //liucheng价格模块隐藏于显示
    public final ObservableField<Integer> itemLiuChengPriceBlockVisiblity = new ObservableField<>();
    //xiaofei价格模块隐藏于显示
    public final ObservableField<Integer> itemXiaoFeiPriceBlockVisiblity = new ObservableField<>();
    //yuri价格模块隐藏于显示
    public final ObservableField<Integer> itemYuriPriceBlockVisiblity = new ObservableField<>();


    public AddNewModel() {
        isAverageUserChecked.set(true);
        whichOnePay.set(-1);
        isLiuChengIn.set(true);
        isXiaoFeiIn.set(true);
        isYuriIn.set(true);
        updateDependentViews();
        updateItemPriceBlock();
        hookUpDependencies();
    }

    private void hookUpDependencies() {
        isAverageUserChecked.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                Log.d("isAverageUserChecked");
                updateDependentViews();
            }
        });

        isLiuChengIn.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Log.d("isLiuChengIn");
                if (isAverageUserChecked.get()) {
                    return;
                }
                itemLiuChengPriceBlockVisiblity.set(isLiuChengIn.get() ? View.VISIBLE : View.GONE);
            }
        });
        isXiaoFeiIn.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Log.d("isXiaoFeiIn");
                if (isAverageUserChecked.get()) {
                    return;
                }
                itemXiaoFeiPriceBlockVisiblity.set(isXiaoFeiIn.get() ? View.VISIBLE : View.GONE);
            }
        });
        isYuriIn.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Log.d("isYuriIn");
                if (isAverageUserChecked.get()) {
                    return;
                }
                itemYuriPriceBlockVisiblity.set(isYuriIn.get() ? View.VISIBLE : View.GONE);
            }
        });

        whichOnePay.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Log.d();
            }
        });
    }

    public void updateDependentViews() {
        Log.d();
        updateDependentViews(isAverageUserChecked.get());
    }

    public void updateDependentViews(boolean isAverageUserChecked) {
        Log.d("isAverageUserChecked:" + isAverageUserChecked);
        if (isAverageUserChecked) {
            itemPriceBlockVisiblity.set(View.GONE);
        } else {
            itemPriceBlockVisiblity.set(View.VISIBLE);
            updateItemPriceBlock();
        }
    }

    public void updateItemPriceBlock() {
        itemLiuChengPriceBlockVisiblity.set(isLiuChengIn.get() ? View.VISIBLE : View.GONE);
        itemXiaoFeiPriceBlockVisiblity.set(isXiaoFeiIn.get() ? View.VISIBLE : View.GONE);
        itemYuriPriceBlockVisiblity.set(isYuriIn.get() ? View.VISIBLE : View.GONE);
    }

}

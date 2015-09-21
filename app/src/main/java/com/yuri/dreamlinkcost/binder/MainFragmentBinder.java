package com.yuri.dreamlinkcost.binder;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.yuri.dreamlinkcost.BR;

public class MainFragmentBinder extends BaseObservable {

    private String emptyMsg;
    private boolean dataEmpty;
    private boolean loading;

    @Bindable
    public String getEmptyMsg() {
        return emptyMsg;
    }

    public void setEmptyMsg(String emptyMsg) {
        this.emptyMsg = emptyMsg;
        notifyPropertyChanged(BR.emptyMsg);
    }

    @Bindable
    public boolean isDataEmpty() {
        return dataEmpty;
    }

    public void setIsDataEmpty(boolean isDataEmpty) {
        this.dataEmpty = isDataEmpty;
        notifyPropertyChanged(BR.dataEmpty);
    }

    @Bindable
    public boolean isLoading() {
        return loading;
    }

    public void setIsLoading(boolean isLoading) {
        this.loading = isLoading;
        notifyPropertyChanged(BR.loading);
    }
}

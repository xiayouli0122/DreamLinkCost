package com.yuri.dreamlinkcost.model.impl;

import android.content.Context;

import com.yuri.dreamlinkcost.model.OnLoginListener;

/**
 * Created by Yuri on 2016/1/15.
 */
public interface IUserLogin {
    void autoLogin(Context context, OnLoginListener listener);
    void login(Context context, int userId, OnLoginListener listener);
}

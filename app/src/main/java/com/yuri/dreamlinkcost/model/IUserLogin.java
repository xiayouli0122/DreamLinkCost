package com.yuri.dreamlinkcost.model;

import android.content.Context;

/**
 * Created by Yuri on 2016/1/15.
 */
public interface IUserLogin {
    void autoLogin(Context context, OnLoginListener listener);
    void login(Context context, int userId, OnLoginListener listener);
}

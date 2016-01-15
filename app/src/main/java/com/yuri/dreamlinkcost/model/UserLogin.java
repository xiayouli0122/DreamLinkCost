package com.yuri.dreamlinkcost.model;

import android.content.Context;

import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.SharedPreferencesManager;
import com.yuri.dreamlinkcost.Utils;

/**
 * Created by Yuri on 2016/1/15.
 */
public class UserLogin implements IUserLogin {

    @Override
    public void autoLogin(Context context, OnLoginListener listener) {
        if (listener == null) {
            throw new NullPointerException("OnLoginListener cannot be null");
        }
        //实现自动登录的操作
        int currentVersionCode = Utils.getVersionCode(context);
        int versionCode = SharedPreferencesManager.get(context, Constant.Extra.KEY_VERSION_CODE, -1);
        if (versionCode == -1) {
            SharedPreferencesManager.put(context, Constant.Extra.KEY_VERSION_CODE, currentVersionCode);
        } else {
            if (versionCode != currentVersionCode) {
                SharedPreferencesManager.put(context, Constant.Extra.KEY_VERSION_CODE, currentVersionCode);
            }
        }
        int author = SharedPreferencesManager.get(context, Constant.Extra.KEY_LOGIN, -1);
        if (author != -1) {
            listener.onLoginSuccess();
        } else {
            listener.onLoginFail();
        }
    }

    @Override
    public void login(Context context, int userId, OnLoginListener listener) {
        if (listener == null) {
            throw new NullPointerException("OnLoginListener cannot be null");
        }
        //实现点击登陆操作
        SharedPreferencesManager.put(context, Constant.Extra.KEY_LOGIN, userId);
        listener.onLoginSuccess();
    }
}

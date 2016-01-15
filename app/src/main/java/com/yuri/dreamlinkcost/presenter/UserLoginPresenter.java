package com.yuri.dreamlinkcost.presenter;

import android.content.Context;

import com.yuri.dreamlinkcost.model.IUserLogin;
import com.yuri.dreamlinkcost.model.OnLoginListener;
import com.yuri.dreamlinkcost.model.UserLogin;
import com.yuri.dreamlinkcost.view.impl.IUserLoginView;

/**
 * Created by Yuri on 2016/1/15.
 */
public class UserLoginPresenter {

    private Context context;
    private IUserLogin userLogin;
    private IUserLoginView userLoginView;

    public UserLoginPresenter(Context context, IUserLoginView iUserLoginView) {
        this.context = context;
        this.userLoginView = iUserLoginView;
        this.userLogin = new UserLogin();
    }

    public void doAutoLogin() {
        userLogin.autoLogin(context, new OnLoginListener() {
            @Override
            public void onLoginSuccess() {
                userLoginView.goToMainActivity();
            }

            @Override
            public void onLoginFail() {
                //do nothing
            }
        });
    }

    public void doLogin(int userId) {
        userLogin.login(context, userId, new OnLoginListener() {
            @Override
            public void onLoginSuccess() {
                userLoginView.goToMainActivity();//通知View
            }

            @Override
            public void onLoginFail() {
                //ignore
            }
        });
    }
}

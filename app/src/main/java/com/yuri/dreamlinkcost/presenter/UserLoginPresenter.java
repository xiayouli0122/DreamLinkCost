package com.yuri.dreamlinkcost.presenter;

import android.content.Context;

import com.yuri.dreamlinkcost.model.impl.IUserLogin;
import com.yuri.dreamlinkcost.model.OnLoginListener;
import com.yuri.dreamlinkcost.model.UserLogin;
import com.yuri.dreamlinkcost.view.impl.IUserLoginView;
import com.yuri.xlog.Log;

/**
 * Created by Yuri on 2016/1/15.
 */
public class UserLoginPresenter extends BasePresenter<IUserLoginView>{

    private IUserLogin userLogin;

    public UserLoginPresenter(Context context, IUserLoginView iUserLoginView) {
        super(context, iUserLoginView);
        this.userLogin = new UserLogin();
    }

    public void doAutoLogin() {
        userLogin.autoLogin(mContext, new OnLoginListener() {
            @Override
            public void onLoginSuccess() {
                mView.onLoginSuccess();
            }

            @Override
            public void onLoginFail() {
                //do nothing
            }
        });
    }

    public void doLogin(int userId) {
        Log.d("userId:" + userId);
        userLogin.login(mContext, userId, new OnLoginListener() {
            @Override
            public void onLoginSuccess() {
                mView.onLoginSuccess();//通知View
            }

            @Override
            public void onLoginFail() {
                //ignore
                mView.showError("登录失败");
            }
        });
    }
}

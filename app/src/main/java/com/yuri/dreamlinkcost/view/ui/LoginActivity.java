package com.yuri.dreamlinkcost.view.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.databinding.LoginBinder;
import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.presenter.UserLoginPresenter;
import com.yuri.dreamlinkcost.view.impl.IUserLoginView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, IUserLoginView {

    /**
     * 采用MVP模式之后，在View层，也就是Activity界面看不到任何关于数据操作的代码
     *
     * 缺点就是会多些很多其他代码，多创建很多类
     */
    private UserLoginPresenter mUserLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoginBinder loginBinder = DataBindingUtil.setContentView(this, R.layout.activity_login);

        loginBinder.tvLiucheng.setOnClickListener(this);
        loginBinder.tvXiaofei.setOnClickListener(this);
        loginBinder.tvYuri.setOnClickListener(this);

        mUserLoginPresenter = new UserLoginPresenter(getApplicationContext(), this);
        mUserLoginPresenter.doAutoLogin();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_liucheng:
                mUserLoginPresenter.doLogin(Constant.Author.LIUCHENG);
                break;
            case R.id.tv_xiaofei:
                mUserLoginPresenter.doLogin(Constant.Author.XIAOFEI);
                break;
            case R.id.tv_yuri:
                mUserLoginPresenter.doLogin(Constant.Author.YURI);
                break;
        }
    }

    @Override
    public void goToMainActivity() {
        Log.d();
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}

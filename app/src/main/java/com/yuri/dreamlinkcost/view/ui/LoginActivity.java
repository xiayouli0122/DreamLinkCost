package com.yuri.dreamlinkcost.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.presenter.UserLoginPresenter;
import com.yuri.dreamlinkcost.utils.ToastUtil;
import com.yuri.dreamlinkcost.view.impl.IUserLoginView;
import com.yuri.xlog.Log;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, IUserLoginView {

    /**
     * 采用MVP模式之后，在View层，也就是Activity界面看不到任何关于数据操作的代码
     */
    private UserLoginPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mPresenter = new UserLoginPresenter(getApplicationContext(), this);
        mPresenter.doAutoLogin();
    }

    @OnClick({R.id.tv_liucheng, R.id.tv_xiaofei, R.id.tv_yuri})
    public void onClick(View view) {
        Log.d();
        switch (view.getId()) {
            case R.id.tv_liucheng:
                mPresenter.doLogin(Constant.Author.LIUCHENG);
                break;
            case R.id.tv_xiaofei:
                mPresenter.doLogin(Constant.Author.XIAOFEI);
                break;
            case R.id.tv_yuri:
                mPresenter.doLogin(Constant.Author.YURI);
                break;
        }
    }

    @Override
    public void onLoginSuccess() {
        goToMainActivity();
    }

    @Override
    public void showError(String message) {
        ToastUtil.showToast(getApplicationContext(), message);
    }

    public void goToMainActivity() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}

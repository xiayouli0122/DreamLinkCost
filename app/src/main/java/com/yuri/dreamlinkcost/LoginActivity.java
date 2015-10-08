package com.yuri.dreamlinkcost;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yuri.dreamlinkcost.databinding.LoginBinder;
import com.yuri.dreamlinkcost.log.Log;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LoginBinder mLoginBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginBinder = DataBindingUtil.setContentView(this, R.layout.activity_login);
        init();
    }

    public void init() {
        int currentVersionCode = Utils.getVersionCode(this);
        int versionCode = SharedPreferencesManager.get(this, Constant.Extra.KEY_VERSION_CODE, -1);
        if (versionCode == -1) {
            SharedPreferencesManager.put(this, Constant.Extra.KEY_VERSION_CODE, currentVersionCode);
        } else {
            if (versionCode != currentVersionCode) {
                SharedPreferencesManager.put(this, Constant.Extra.KEY_VERSION_CODE, currentVersionCode);
            }
        }

        int author = SharedPreferencesManager.get(this, Constant.Extra.KEY_LOGIN, -1);
        if (author != -1) {
            goToMain();
        }
        mLoginBinder.tvLiucheng.setOnClickListener(this);
        mLoginBinder.tvXiaofei.setOnClickListener(this);
        mLoginBinder.tvYuri.setOnClickListener(this);

    }

    public void goToMain() {
        Log.d();
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_liucheng:
                SharedPreferencesManager.put(this, Constant.Extra.KEY_LOGIN, Constant.Author.LIUCHENG);
                break;
            case R.id.tv_xiaofei:
                SharedPreferencesManager.put(this, Constant.Extra.KEY_LOGIN, Constant.Author.XIAOFEI);
                break;
            case R.id.tv_yuri:
                SharedPreferencesManager.put(this, Constant.Extra.KEY_LOGIN, Constant.Author.YURI);
                break;
        }
        goToMain();
    }
}

package com.yuri.dreamlinkcost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Yuri on 2015/7/14.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity {

    @ViewById(R.id.tv_liucheng)
    TextView mLiuChengView;
    @ViewById(R.id.tv_xiaofei)
    TextView mXiaoFeiView;
    @ViewById(R.id.tv_yuri)
    TextView mYuriView;

    SharedPreferences mSharedPreference;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @AfterViews
    public void init() {
        mSharedPreference = getSharedPreferences(Constant.SHARED_NAME, MODE_PRIVATE);
        int author = mSharedPreference.getInt(Constant.Extra.KEY_LOGIN, -1);
        if (author != -1) {
            goToMain();
        }
    }

    @Click(value = R.id.tv_liucheng)
    public void doLiuChengLogin() {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putInt(Constant.Extra.KEY_LOGIN, Constant.Author.LIUCHENG);
        editor.commit();
        goToMain();
    }

    @Click(value = R.id.tv_xiaofei)
    public void doXiaoFeiLogin() {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putInt(Constant.Extra.KEY_LOGIN, Constant.Author.XIAOFEI);
        editor.commit();
        goToMain();
    }

    @Click(value = R.id.tv_yuri)
    public void doYuriLogin() {
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.putInt(Constant.Extra.KEY_LOGIN, Constant.Author.YURI);
        editor.commit();
        goToMain();
    }

    public void goToMain() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity_.class);
        startActivity(intent);
        this.finish();
    }
}

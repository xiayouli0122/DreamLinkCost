package com.yuri.dreamlinkcost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.activeandroid.query.Select;
import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.model.Cost;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        init();
    }

    public void init() {
        int currentVersionCode = Utils.getVersionCode(this);
        int versionCode = SharedPreferencesManager.get(this, Constant.Extra.KEY_VERSION_CODE, -1);
        if (versionCode == -1) {
            if (currentVersionCode == 40) {
                doVersion40Change();
            }
            SharedPreferencesManager.put(this, Constant.Extra.KEY_VERSION_CODE, currentVersionCode);
        } else {
            if (versionCode != currentVersionCode) {
                //新版本升级，第一次启动
                if (currentVersionCode == 40) {
                    doVersion40Change();
                }
                SharedPreferencesManager.put(this, Constant.Extra.KEY_VERSION_CODE, currentVersionCode);
            }
        }

        int author = SharedPreferencesManager.get(this, Constant.Extra.KEY_LOGIN, -1);
        if (author != -1) {
            goToMain();
        }
    }

    private void doVersion40Change() {
        //4.0版本对本地数据库作了改变，只保留未提交到服务器的数据
        List<Cost> costs = new Select().from(Cost.class).execute();
        for (Cost cost : costs) {
            if (cost.status == Constant.STATUS_COMMIT_SUCCESS) {
                cost.delete();
            }
        }
    }

    @OnClick(R.id.tv_liucheng)
    public void doLiuChengLogin() {
        SharedPreferencesManager.put(this, Constant.Extra.KEY_LOGIN, Constant.Author.LIUCHENG);
        goToMain();
    }

    @OnClick(R.id.tv_xiaofei)
    public void doXiaoFeiLogin() {
        SharedPreferencesManager.put(this, Constant.Extra.KEY_LOGIN, Constant.Author.XIAOFEI);
        goToMain();
    }

    @OnClick(R.id.tv_yuri)
    public void doYuriLogin() {
        SharedPreferencesManager.put(this, Constant.Extra.KEY_LOGIN, Constant.Author.YURI);
        goToMain();
    }

    public void goToMain() {
        Log.d();
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}

package com.yuri.dreamlinkcost.view.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.utils.permission.PermissionCallback;
import com.yuri.dreamlinkcost.utils.permission.PermissionManager;

import java.lang.ref.WeakReference;

import tyrantgit.explosionfield.ExplosionField;

public class SplashActivity2 extends AppCompatActivity {

    private ExplosionField mExplosionField;

    private ImageView mImageView;

    private MyHandler mHandler;

    private static final int MSG_START_ANIM = 0;
    private static final int MSG_END_SPLASH = 1;
    private static class MyHandler extends Handler {
        private WeakReference<SplashActivity2> mOuter;

        public MyHandler(SplashActivity2 activity) {
            mOuter = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SplashActivity2 activity = mOuter.get();
            switch (msg.what) {
                case MSG_START_ANIM:
                    activity.startAnim();
                    break;
                case MSG_END_SPLASH:
                    activity.finishSplash();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mExplosionField = ExplosionField.attach2Window(this);

        mImageView = (ImageView) findViewById(R.id.iv_logo);

        mHandler = new MyHandler(this);

        if (!PermissionManager.isAndroidM()) {
            //6.0以下直接开始
            startInit();
        } else {
            requestExternalPermission();
        }
    }



    /**
     * android 6.0以后 启动 请求必要权限，暂时只有sdcard的权限是必须的
     */
    private void requestExternalPermission() {
        if (PermissionManager.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //sdcard权限拿到了，就去看一下个
            requestReadPhoneState();
            return;
        }

        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        PermissionManager.askPermission(this, permissions, new PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                //允许使用sdcard权限才能开始应用
                requestReadPhoneState();
            }

            @Override
            public void onPermissionRefused() {
                requestReadPhoneState();
            }
        });
    }

    private void requestReadPhoneState() {
        if (PermissionManager.hasPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            startInit();
            return;
        }

        String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
        PermissionManager.askPermission(this, permissions, new PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                //允许使用sdcard权限才能开始应用
                startInit();
            }

            @Override
            public void onPermissionRefused() {
                new AlertDialog.Builder(SplashActivity2.this)
                        .setMessage("不给权限，不让启动")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create().show();
            }
        });
    }

    private void startInit() {
        mHandler.sendEmptyMessageDelayed(MSG_START_ANIM, 500);
    }

    private void startAnim() {
        if (mExplosionField != null) {
            mExplosionField.explode(mImageView);
        }

        if (mHandler != null) {
            mHandler.sendEmptyMessageDelayed(MSG_END_SPLASH, 1000);
        }
    }

    private void finishSplash() {

        mHandler.removeMessages(MSG_START_ANIM);
        mHandler.removeMessages(MSG_END_SPLASH);

        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        mExplosionField.clear();
        mExplosionField = null;
    }
}

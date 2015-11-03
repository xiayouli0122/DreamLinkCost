package com.yuri.dreamlinkcost;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.bmob.pay.tool.BmobPay;
import com.bmob.pay.tool.PayListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PayActivity extends AppCompatActivity {

    private static final double MONEY = 1.00;
    private static final String PAY_BODY = "打赏Yuri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.btn_alipay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payByAli(v);
            }
        });

        findViewById(R.id.btn_wxpay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                payByWX(v);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void payByAli(final View v) {
        new BmobPay(PayActivity.this).pay(MONEY, PAY_BODY, new PayListener() {
            @Override
            public void orderId(String s) {
                Snackbar.make(v, "获取订单成功!请等待跳转到支付页面~", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void succeed() {
                showPaySuccessDialog();
            }

            @Override
            public void fail(int i, String s) {
                Snackbar.make(v, "支付中断!", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void unknow() {
                Snackbar.make(v, "支付结果未知,请稍后手动查询", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void payByWX(final View v) {
        new BmobPay(PayActivity.this).payByWX(MONEY, PAY_BODY, new PayListener() {
            @Override
            public void orderId(String s) {
                Snackbar.make(v, "获取订单成功!请等待跳转到支付页面~", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void succeed() {
                showPaySuccessDialog();
            }

            @Override
            public void fail(int i, String s) {
                if (i == -3) {
                    //需要安装插件
                    new AlertDialog.Builder(PayActivity.this)
                            .setMessage(
                                    "监测到你尚未安装支付插件,无法进行微信支付,请选择安装插件(已打包在本地,无流量消耗)还是用支付宝支付")
                            .setPositiveButton("安装",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            installBmobPayPlugin("BmobPayPlugin.apk");
                                        }
                                    })
                            .setNegativeButton("支付宝支付",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            payByAli(v);
                                        }
                                    }).create().show();
                } else {
                    Snackbar.make(v, "支付中断", Snackbar.LENGTH_SHORT).show();
                }

            }

            @Override
            public void unknow() {
                Snackbar.make(v, "支付结果未知,请稍后手动查询", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    void installBmobPayPlugin(String fileName) {
        try {
            InputStream is = getAssets().open(fileName);
            File file = new File(Environment.getExternalStorageDirectory()
                    + File.separator + fileName);
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + file),
                    "application/vnd.android.package-archive");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showPaySuccessDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Yuri在此多谢爷的打赏")
                .setPositiveButton("跪安吧", null)
                .create().show();
    }

}

package com.yuri.dreamlinkcost;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.tencent.bugly.crashreport.CrashReport;
import com.yuri.dreamlinkcost.Bmob.BmobCost;
import com.yuri.dreamlinkcost.Bmob.BmobTitle;
import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.model.Cost;
import com.yuri.dreamlinkcost.model.Title;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewById(R.id.fab_button)
    TextView mFabButton;

    @ViewById(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private MainFragment mainFragment;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, Constant.BMOB_APP_ID);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, Constant.BMOB_APP_ID);

        //Bugly
        Log.d();
//        CrashReport.initCrashReport(getApplicationContext(), "900005722", true);
        SharedPreferences mSharedPreference = getSharedPreferences(Constant.SHARED_NAME, MODE_PRIVATE);
        int author = mSharedPreference.getInt(Constant.Extra.KEY_LOGIN, -1);
        if (author == Constant.Author.LIUCHENG) {
            CrashReport.setUserId("LiuCheng");
        } else if (author == Constant.Author.XIAOFEI) {
            CrashReport.setUserId("XiaoFei");
        } else {
            CrashReport.setUserId("Yuri");
        }

        doGetTitleFromNet();
        List<Title> titles = new Select().from(Title.class).execute();
        if (titles == null || titles.size() == 0) {
            String[] titleArrays = getResources().getStringArray(R.array.title_arrays);
            Title title;
            for (int i = 0; i < titleArrays.length; i++) {
                title = new Title();
                title.mTitle = titleArrays[i];
                title.save();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @AfterViews
    public void init() {
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerToggle  = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        FragmentManager fm = getFragmentManager();
        LeftMenuFragment leftMenuFragment = LeftMenuFragment.newInstance("", "");
        fm.beginTransaction().replace(R.id.left_menu_container, leftMenuFragment).commit();

        mainFragment = new MainFragment_();
        fm.beginTransaction().replace(R.id.content_view, mainFragment).commit();
    }

    private void doGetTitleFromNet() {
        BmobQuery<BmobTitle> bmobQuery = new BmobQuery<>();
        bmobQuery.findObjects(getApplicationContext(), new FindListener<BmobTitle>() {
            @Override
            public void onSuccess(List<BmobTitle> list) {
                for (BmobTitle bmobTitle : list) {
                    Title title = new Select().from(Title.class).where("title=?", bmobTitle.title).executeSingle();
                    if (title != null) {
                        //
                    } else {
                        title = bmobTitle.getTitle();
                        title.save();
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Click(R.id.fab_button)
    void doAddNew() {
        Intent intent = new Intent();
        intent.setClass(this, AddNewActivity_.class);
        startActivityForResult(intent, 11);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        List<Cost> list = mainFragment.getCostList();
        int id = item.getItemId();
        switch (id) {
            case R.id.action_statistics:
                float totalPay = 0;
                float liuchengPay = 0;
                float xiaofeiPay = 0;
                float yuriPay = 0;
                for (Cost cos : list) {
                    totalPay += cos.totalPay;
                    liuchengPay += cos.payLC;
                    xiaofeiPay += cos.payXF;
                    yuriPay += cos.payYuri;
                }

                String startTime = Utils.getDate(list.get(list.size() - 1).createDate);
                String endTime = Utils.getDate(list.get(0).createDate);

                StringBuilder sb = new StringBuilder();
                sb.append("开始日期:" + startTime + "\n");
                sb.append("结束日期:" + endTime + "\n\n");
                sb.append("总共消费(¥):" + totalPay + "\n");
                sb.append("LiuCheng(¥):"+ liuchengPay + "\n");
                sb.append("XiaoFei(¥):" + xiaofeiPay + "\n");
                sb.append("Yuri(¥):" + yuriPay + "\n\n");

                sb.append("(注：结算后本地将不再显示已结算的数据，请确认后再操作)");
                showJieSuanDialog("账单结算", sb.toString());
                break;
            case R.id.action_about:
                String title = "About";
                String message = "Version:" + Utils.getAppVersion(this);
                showDialog(title, message);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create().show();
    }

    private void showJieSuanDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("取消", null)
                .setPositiveButton("结算", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("结算中...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setCancelable(false);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.show();
                        List<BmobObject> saveBmobCosts = new ArrayList<>();
                        List<BmobObject> updateBmobCosts = new ArrayList<>();

                        List<Cost> costList = new Select().from(Cost.class).execute();
                        //服务器结算
                        BmobCost bmobCost;
                        for (final Cost cost : costList) {
                            cost.clear = true;
                            cost.save();

                            if (cost.status == Constant.STATUS_COMMIT_FAILURE) {

                                bmobCost = cost.getCostBean();
                                bmobCost.setObjectId(cost.objectId);

                                saveBmobCosts.add(bmobCost);
                            } else {
                                bmobCost = cost.getCostBean();
                                bmobCost.setObjectId(cost.objectId);

                                updateBmobCosts.add(bmobCost);
                            }
                        }

                        if (saveBmobCosts.size() > 0) {
                            new BmobObject().insertBatch(getApplicationContext(), saveBmobCosts, new SaveListener() {
                                @Override
                                public void onSuccess() {
                                    Log.d("insertBatch success");
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Log.d("insertBatch fail:" + s);
                                }
                            });
                        }

                        if (updateBmobCosts.size() > 0) {
                            new BmobObject().updateBatch(getApplicationContext(), updateBmobCosts, new UpdateListener() {
                                @Override
                                public void onSuccess() {
                                    Log.d("updateBatch success");

                                    if (progressDialog != null) {
                                        progressDialog.cancel();
                                    }

                                    if (mainFragment != null) {
                                        mainFragment.doGetDataFromLocal();
                                    }
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    Log.d("updateBatch fail:" + s);
                                    if (progressDialog != null) {
                                        progressDialog.cancel();
                                    }
                                }
                            });
                        }

                    }
                })
                .create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mainFragment != null) {
            mainFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}

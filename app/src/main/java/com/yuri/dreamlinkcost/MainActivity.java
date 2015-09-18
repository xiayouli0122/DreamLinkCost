package com.yuri.dreamlinkcost;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.tencent.bugly.crashreport.CrashReport;
import com.yuri.dreamlinkcost.Bmob.BmobCost;
import com.yuri.dreamlinkcost.Bmob.BmobTitle;
import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.model.Cost;
import com.yuri.dreamlinkcost.model.Title;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


public class MainActivity extends AppCompatActivity implements MainFragment.OnMainFragmentListener, LeftMenuFragment.OnLeftMenuFragmentListener {

    @Bind(R.id.fab_button)
    TextView mFabButton;

    @Bind(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private MainFragment mainFragment;

    ProgressDialog progressDialog;

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Bmob.initialize(this, Constant.BMOB_APP_ID);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, Constant.BMOB_APP_ID);

        //Bugly
        Log.d();
        SharedPreferences mSharedPreference = getSharedPreferences(Constant.SHARED_NAME, MODE_PRIVATE);
        int author = mSharedPreference.getInt(Constant.Extra.KEY_LOGIN, -1);
        if (author == Constant.Author.LIUCHENG) {
            CrashReport.setUserId("LiuCheng");
        } else if (author == Constant.Author.XIAOFEI) {
            CrashReport.setUserId("XiaoFei");
        } else {
            CrashReport.setUserId("Yuri");
        }

        init();

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
    public void init() {
        // Handle Toolbar
        setSupportActionBar(mToolBar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        FragmentManager fm = getFragmentManager();
        LeftMenuFragment leftMenuFragment = LeftMenuFragment.newInstance();
        fm.beginTransaction().replace(R.id.left_menu_container, leftMenuFragment).commit();

        mainFragment = new MainFragment();
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

    @OnClick(R.id.fab_button)
    void doAddNew() {
        Intent intent = new Intent();
        intent.setClass(this, AddNewActivity.class);
        startActivityForResult(intent, 11);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_statistics:
                List<Cost> localList1 = mainFragment.getLocalList();
                if (localList1 != null && localList1.size() > 0) {
                    Toast.makeText(this, "本地有未提交的数据，请先提交本地数据", Toast.LENGTH_SHORT).show();
                    break;
                }
                List<BmobCost> list = mainFragment.getCostList();
                float totalPay = 0;
                float liuchengPay = 0;
                float xiaofeiPay = 0;
                float yuriPay = 0;
                for (BmobCost cos : list) {
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
                sb.append("LiuCheng(¥):" + liuchengPay + "\n");
                sb.append("XiaoFei(¥):" + xiaofeiPay + "\n");
                sb.append("Yuri(¥):" + yuriPay + "\n\n");

                sb.append("(注：结算后本地将不再显示已结算的数据，请确认后再操作)");
                showJieSuanDialog("账单结算", sb.toString(), list);
                break;
            case R.id.action_commit:
                List<Cost> localList = mainFragment.getLocalList();
                if (localList == null || localList.size() == 0) {
                    Toast.makeText(this, "本地没有需要提交的数据", Toast.LENGTH_SHORT).show();
                } else {
                    new CommitTask().execute(localList);
                }
                break;
            case R.id.action_about:
                String title = "About";
                String message = "Version:" + Utils.getAppVersion(this);
                showDialog(title, message);
                break;
            case R.id.action_data_bind_test:
                Intent intent = new Intent();
                intent.setClass(this, DataBindTestActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLeftMenuItemClick(int position) {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        switch (position) {
            case 0:
                mainFragment.showAll();
                break;
            case 1:
                mainFragment.showAuthor(Constant.Author.LIUCHENG);
                break;
            case 2:
                mainFragment.showAuthor(Constant.Author.XIAOFEI);
                break;
            case 3:
                mainFragment.showAuthor(Constant.Author.YURI);
                break;
        }
    }

    private class CommitTask extends AsyncTask<List<Cost>, Void, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("提交本地数据中...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(List<Cost>... lists) {

            BmobCost bmobCost;
            for (final Cost cost : lists[0]) {
                bmobCost = cost.getCostBean();
                bmobCost.save(getApplicationContext(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("Yuri", "upload success:" + cost.title);
                        //上传成功后，删除本地数据
                        cost.delete();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.d("Yuri", "upload failure:" + cost.title);
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d();
            mainFragment.refresh();
        }
    }

    private void showDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create().show();
    }

    private void showJieSuanDialog(String title, String message, final List<BmobCost> list) {
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

                        List<BmobObject> updateBmobCosts = new ArrayList<>();
                        //服务器结算
                        for (BmobCost bmobCost : list) {
                            updateBmobCosts.add(bmobCost);
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
                                        mainFragment.refresh();
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

    @Override
    public void onUpdateMoney(String detail) {
        mToolBar.setSubtitle(detail);
    }
}

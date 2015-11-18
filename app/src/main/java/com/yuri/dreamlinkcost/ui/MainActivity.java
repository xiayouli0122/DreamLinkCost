package com.yuri.dreamlinkcost.ui;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.bmob.BTPFileResponse;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.DownloadListener;
import com.bmob.btp.callback.UploadListener;
import com.bmob.pay.tool.BmobPay;
import com.tencent.bugly.crashreport.CrashReport;
import com.yuri.dreamlinkcost.Bmob.BmobCost;
import com.yuri.dreamlinkcost.Bmob.BmobTitle;
import com.yuri.dreamlinkcost.Bmob.Version;
import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.SharedPreferencesManager;
import com.yuri.dreamlinkcost.Utils;
import com.yuri.dreamlinkcost.databinding.ActivityMainBinding;
import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.model.Cost;
import com.yuri.dreamlinkcost.model.Title;
import com.yuri.dreamlinkcost.notification.MMNotificationManager;
import com.yuri.dreamlinkcost.notification.NotificationBuilder;
import com.yuri.dreamlinkcost.notification.NotificationReceiver;
import com.yuri.dreamlinkcost.notification.pendingintent.ClickPendingIntentBroadCast;
import com.yuri.dreamlinkcost.rx.RxBus;
import com.yuri.dreamlinkcost.rx.RxBusTag;

import java.io.File;
import java.lang.ref.WeakReference;
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


public class MainActivity extends AppCompatActivity implements MainFragment.OnMainFragmentListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    private MainFragment mainFragment;

    ProgressDialog progressDialog;

    Toolbar mToolBar;

    private UIHandler mUIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BmobPay.init(this, Constant.BMOB_APP_ID);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.fabButton.setOnClickListener(this);
        mDrawerLayout = binding.drawerLayout;
        mNavigationView = binding.navigationView;
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().findItem(R.id.action_all).setChecked(true);
        mNavigationView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

        mToolBar = binding.toolbar;
        mToolBar.setTitle(R.string.app_name);

        mUIHandler = new UIHandler(this);

        Bmob.initialize(this, Constant.BMOB_APP_ID);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, Constant.BMOB_APP_ID);

        //Bugly
        Log.d();
        int author = SharedPreferencesManager.get(this, Constant.Extra.KEY_LOGIN, -1);
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
        checkUpdate(false);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void init() {
        // Handle Toolbar
        setSupportActionBar(mToolBar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        FragmentManager fm = getFragmentManager();
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
        int author = SharedPreferencesManager.get(this, Constant.Extra.KEY_LOGIN, -1);
        if (author == Constant.Author.YURI) {
            getMenuInflater().inflate(R.menu.menu_main_yuri, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
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
            case R.id.action_sort_date:
                SharedPreferencesManager.put(getApplicationContext(), Constant.Extra.KEY_SORT, 0);
                RxBus.get().post(RxBusTag.TAG_MAIN_FRAGEMNT, MainFragment.SORT_BY_DATE);
                break;
            case R.id.action_sort_price:
                SharedPreferencesManager.put(getApplicationContext(), Constant.Extra.KEY_SORT, 1);
                RxBus.get().post(RxBusTag.TAG_MAIN_FRAGEMNT, MainFragment.SORT_BY_PRICE);
                break;
            case R.id.action_upload_new_version:
                doUpload();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_button:
                doAddNew();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        switch (menuItem.getItemId()) {
            case R.id.action_all:
                menuItem.setChecked(true);
                mainFragment.showAll();
                break;
            case R.id.action_liucheng:
                menuItem.setChecked(true);
                mainFragment.showAuthor(Constant.Author.LIUCHENG);
                break;
            case R.id.action_xiaofei:
                menuItem.setChecked(true);
                mainFragment.showAuthor(Constant.Author.XIAOFEI);
                break;
            case R.id.action_yuri:
                menuItem.setChecked(true);
                mainFragment.showAuthor(Constant.Author.YURI);
                break;
            case R.id.action_jiesuan:
                doStatistics();
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
            case R.id.action_check_update:
                checkUpdate(true);
                break;
            case R.id.action_encourage:
                Intent intent = new Intent();
                intent.setClass(this, PayActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void doStatistics() {
        List<Cost> localList1 = mainFragment.getLocalList();
        if (localList1 != null && localList1.size() > 0) {
            Toast.makeText(this, "本地有未提交的数据，请先提交本地数据", Toast.LENGTH_SHORT).show();
            return;
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
    }

    private void checkUpdate(final boolean byuser) {
        //第一步检查本地新版本
        int versionCode = SharedPreferencesManager.get(getApplicationContext(), "latest_version_code", -1);
        int currentVersionCode = Utils.getVersionCode(getApplicationContext());
        if (versionCode > currentVersionCode) {
            //有新版本已经下载好了，可以直接安装
            String apkPath = SharedPreferencesManager.get(getApplicationContext(), "apkPath", null);
            if (apkPath != null && new File(apkPath).exists()) {
                //显示安装Dialog
                showInstallDialog(apkPath);
                return;
            }
        }
        BmobQuery<Version> bmobQuery = new BmobQuery();
        bmobQuery.findObjects(this, new FindListener<Version>() {
            @Override
            public void onSuccess(List<Version> list) {
                if (list != null && list.size() > 0) {
                    int serverVersionCode = list.get(0).version_code;
                    int currentVersionCode = Utils.getVersionCode(getApplicationContext());
                    Log.d("serverVersionCode:" + serverVersionCode + ",currentVersionCode:"
                            + currentVersionCode);
                    if (serverVersionCode > currentVersionCode) {
                        //有新版本了
                        Log.d("Need to update");
                        mUIHandler.sendMessage(mUIHandler.obtainMessage(MSG_UPDATE_VERSION_VIEW));
                        String url = list.get(0).apkUrl;
                        String serverVersion = list.get(0).version;
                        //has new version
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("version", serverVersion);
                        bundle.putInt("version_code", serverVersionCode);
                        bundle.putString("url", url);
                        message.setData(bundle);

                        if (byuser) {
                            message.what = MSG_SHOW_UPDATE_NOTIFICATION;
                        } else {
                            if (Utils.isWifiConnected(getApplicationContext())) {
                                message.what = MSG_BACKGROUND_DOWNLOAD;
                            } else {
                                message.what = MSG_SHOW_UPDATE_NOTIFICATION;
                            }
                        }
                        mUIHandler.sendMessage(message);
                    } else {
                        if (byuser) {
                            mUIHandler.sendMessage(mUIHandler.obtainMessage(MSG_NO_VERSION_UPDATE));
                        }
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void showInstallDialog(final String apkPath) {
        new AlertDialog.Builder(this)
                .setMessage("新版本已经后台下载完成。")
                .setPositiveButton("立即安装", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent installIntent = new Intent(Intent.ACTION_VIEW);
                        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        installIntent.setDataAndType(Uri.parse("file://" + apkPath),
                                "application/vnd.android.package-archive");
                        startActivity(installIntent);
                    }
                })
                .setNegativeButton("稍后再说", null)
                .create().show();
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

    private static final int MSG_UPDATE_VERSION_VIEW = 0;
    private static final int MSG_NO_VERSION_UPDATE = 1;
    private static final int MSG_SHOW_UPDATE_NOTIFICATION = 2;
    private static final int MSG_BACKGROUND_DOWNLOAD = 3;
    private static class UIHandler extends Handler{
        private WeakReference<MainActivity> mOuter;

        public UIHandler(MainActivity activity) {
            mOuter = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mOuter.get();
            switch (msg.what) {
                case MSG_NO_VERSION_UPDATE:
                    Toast.makeText(activity, "已经是最新版本了", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_UPDATE_VERSION_VIEW:
                    break;
                case MSG_SHOW_UPDATE_NOTIFICATION:
                    String version = msg.getData().getString("version");
                    String fileName = msg.getData().getString("url");
                    activity.showUpdateNotification(version, fileName);
                    break;
                case MSG_BACKGROUND_DOWNLOAD:
                    String version1 = msg.getData().getString("version");
                    int versionCode1 = msg.getData().getInt("version_code");
                    String fileName1 = msg.getData().getString("url");
                    activity.doDownloadNewVersionAPk(version1, versionCode1, fileName1);
                    break;
            }
        }
    };

    //自动后台下载新版本apk，下载完毕后直接提醒用户
    public void doDownloadNewVersionAPk(final String version, final int versionCode, final String fileName) {
        BmobProFile.getInstance(getApplicationContext()).download(fileName, new DownloadListener() {
            @Override
            public void onSuccess(String s) {
                Log.d("Download Apk Success.localPath:" + s);
                SharedPreferencesManager.put(getApplicationContext(), "latest_version_code", versionCode);
                SharedPreferencesManager.put(getApplicationContext(), "apkPath", s);
                showInstallNotification(version);
            }

            @Override
            public void onProgress(String s, int i) {

            }

            @Override
            public void onError(int i, String s) {
                Log.e("Download apk faile:" + s);
            }
        });

//        Observable<String> observable = Observable.just(fileName)
//                .map(new Func1<String, String>() {
//                    @Override
//                    public String call(String s) {
//                        Log.d(s);
//                        downlaod(fileName);
//                        return null;
//                    }
//                });
//        observable.subscribe(new Action1<String>() {
//            @Override
//            public void call(String s) {
//                Log.d(s);
//            }
//        });
    }

    private void showInstallNotification(String serverVersion) {
        NotificationBuilder builder = MMNotificationManager.getInstance(getApplicationContext()).load();
        builder.setNotificationId(Constant.NotificationID.VERSION_UPDAET);
        builder.setContentTitle("新版本已经下载完毕，点击立即安装");
        ClickPendingIntentBroadCast cancelBroadcast = new ClickPendingIntentBroadCast(
                NotificationReceiver.ACTION_NOTIFICATION_CANCEL);
        Bundle bundle = new Bundle();
        bundle.putInt("id", Constant.NotificationID.VERSION_UPDAET);
        cancelBroadcast.setBundle(bundle);

        ClickPendingIntentBroadCast installBroadcast = new ClickPendingIntentBroadCast(
                NotificationReceiver.ACTION_NOTIFICATION_INSTALL_APP);
        builder.setProfit(NotificationCompat.PRIORITY_MAX);
        builder.addAction(R.mipmap.ic_cancel, "稍后查看", cancelBroadcast);
        builder.addAction(R.mipmap.ic_download, "立即安装", installBroadcast);

        builder.setFullScreenIntent(PendingIntent.getBroadcast(getApplicationContext(), 0,
                new Intent(NotificationReceiver.ACTION_NOTIFICATION_CLICK_INTENT), PendingIntent.FLAG_UPDATE_CURRENT), true);

        builder.getSimpleNotification().build(true);
    }

    private void showUpdateNotification(String serverVersion,  String url) {
        NotificationBuilder builder = MMNotificationManager.getInstance(getApplicationContext()).load();
        builder.setNotificationId(Constant.NotificationID.VERSION_UPDAET);
        builder.setContentTitle("有新版本了:" + serverVersion);
        ClickPendingIntentBroadCast cancelBroadcast = new ClickPendingIntentBroadCast(
                NotificationReceiver.ACTION_NOTIFICATION_CANCEL);
        Bundle bundle = new Bundle();
        bundle.putInt("id", Constant.NotificationID.VERSION_UPDAET);
        cancelBroadcast.setBundle(bundle);

        ClickPendingIntentBroadCast downloadBroadcast = new ClickPendingIntentBroadCast(NotificationReceiver.ACTION_NOTIFICATION_VERSION_UPDATE);
        Bundle bundle1 = new Bundle();
        bundle1.putString("versionUrl", url);
        downloadBroadcast.setBundle(bundle1);
        builder.setProfit(NotificationCompat.PRIORITY_MAX);
        builder.addAction(R.mipmap.ic_cancel, "稍后查看", cancelBroadcast);
        builder.addAction(R.mipmap.ic_download, "立即下载", downloadBroadcast);

        builder.setFullScreenIntent(PendingIntent.getBroadcast(getApplicationContext(), 0,
                new Intent(NotificationReceiver.ACTION_NOTIFICATION_CLICK_INTENT), PendingIntent.FLAG_UPDATE_CURRENT), true);

        builder.getSimpleNotification().build(true);
    }

    private void doUpload() {
        String filePath = "/sdcard/Release_V" + Utils.getAppVersion(getApplicationContext())  + ".apk";
        if (!new File(filePath).exists()) {
            Toast.makeText(getApplicationContext(), filePath + " is not exist", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog progressDialog  = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("上传文件中..." + filePath);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.show();
        BTPFileResponse response = BmobProFile.getInstance(getApplicationContext()).upload(filePath, new UploadListener() {
            @Override
            public void onSuccess(String s, String s1) {
                Log.d("success.name:" + s + ",url:" + s1);
                progressDialog.setProgress(100);
                progressDialog.setMessage("上传完成");
                progressDialog.cancel();

                Version version =new Version();
                version.version = Utils.getAppVersion(getApplicationContext());
                version.version_code = Utils.getVersionCode(getApplicationContext());
                version.apkUrl = s;
                version.update(getApplicationContext(), "692ZQQQp", new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        Log.d();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Log.d();
                    }
                });
            }

            @Override
            public void onProgress(int i) {
                Log.d("progress：" + i);
                progressDialog.setProgress(i);
            }

            @Override
            public void onError(int i, String s) {
                Log.d("error:" + s);
                progressDialog.setMessage("上传失败:" + s);
                progressDialog.cancel();
            }
        });
    }
}

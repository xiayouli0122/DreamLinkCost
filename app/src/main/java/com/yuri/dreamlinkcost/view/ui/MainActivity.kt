package com.yuri.dreamlinkcost.view.ui

import android.annotation.TargetApi
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.NavigationView
import android.support.v4.app.NotificationCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobBatch
import cn.bmob.v3.BmobObject
import cn.bmob.v3.datatype.BatchResult
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.QueryListListener
import com.tencent.bugly.crashreport.CrashReport
import com.yuri.dreamlinkcost.BuildConfig
import com.yuri.dreamlinkcost.Constant
import com.yuri.dreamlinkcost.R
import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri
import com.yuri.dreamlinkcost.model.CommitResultListener
import com.yuri.dreamlinkcost.model.Main
import com.yuri.dreamlinkcost.notification.MMNotificationManager
import com.yuri.dreamlinkcost.notification.NotificationReceiver
import com.yuri.dreamlinkcost.notification.pendingintent.ClickPendingIntentBroadCast
import com.yuri.dreamlinkcost.presenter.MainPresenter
import com.yuri.dreamlinkcost.rx.RxBus
import com.yuri.dreamlinkcost.rx.RxBusTag
import com.yuri.dreamlinkcost.utils.SharedPreferencesUtil
import com.yuri.dreamlinkcost.utils.TimeUtil
import com.yuri.dreamlinkcost.view.impl.IMainView
import com.yuri.xlog.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.lang.ref.WeakReference
import java.util.*


class MainActivity : AppCompatActivity(), MainFragment.OnMainFragmentListener, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, IMainView {

    private var progressDialog: ProgressDialog? = null

    private var mainFragment: MainFragment? = null
    private var mUIHandler: UIHandler? = null

    private var mMainPresenter: MainPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        navigationView!!.setNavigationItemSelectedListener(this)
        navigationView!!.menu.findItem(R.id.action_all).isChecked = true
        navigationView!!.overScrollMode = View.OVER_SCROLL_ALWAYS


        mUIHandler = UIHandler(this)

        mMainPresenter = MainPresenter(applicationContext, this)

        Bmob.initialize(this, Constant.BMOB_APP_ID)
        //2016年4月6日09:21:05 bmob的push暂时出现了bug，
        //        // 使用推送服务时的初始化操作
        //        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        //        BmobPush.startWork(this, Constant.BMOB_APP_ID);


        //Bugly
        CrashReport.setUserId("Yuri")
        toolbar!!.title = getString(R.string.app_name)

        init()

        mMainPresenter!!.initTitles()

        mMainPresenter!!.checkUpdate(false, mUIHandler)

        fab_button.setOnClickListener(this@MainActivity)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun init() {
        // Handle Toolbar
        setSupportActionBar(toolbar)

        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name)
        drawerToggle.syncState()
        drawerLayout!!.setDrawerListener(drawerToggle)

        val fm = supportFragmentManager
        mainFragment = MainFragment()
        fm.beginTransaction().replace(R.id.content_view, mainFragment).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val author = mMainPresenter!!.userId
        if (author == Constant.Author.YURI) {
            menuInflater.inflate(R.menu.menu_main_yuri, menu)
        } else {
            menuInflater.inflate(R.menu.menu_main, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_sort_date_asc -> {
                SharedPreferencesUtil.put(applicationContext, Constant.Extra.KEY_SORT, 0)
                RxBus.get().post(RxBusTag.TAG_MAIN_FRAGEMNT, MainFragment.SORT_BY_DATE_ASC)
            }
            R.id.action_sort_default//默认排序即按时间升序
                , R.id.action_sort_date_desc -> {
                SharedPreferencesUtil.put(applicationContext, Constant.Extra.KEY_SORT, 1)
                RxBus.get().post(RxBusTag.TAG_MAIN_FRAGEMNT, MainFragment.SORT_BY_DATE_DESC)
            }
            R.id.action_sort_price_asc -> {
                SharedPreferencesUtil.put(applicationContext, Constant.Extra.KEY_SORT, 2)
                RxBus.get().post(RxBusTag.TAG_MAIN_FRAGEMNT, MainFragment.SORT_BY_PRICE_ASC)
            }
            R.id.action_sort_price_desc -> {
                SharedPreferencesUtil.put(applicationContext, Constant.Extra.KEY_SORT, 3)
                RxBus.get().post(RxBusTag.TAG_MAIN_FRAGEMNT, MainFragment.SORT_BY_PRICE_DESC)
            }
            R.id.action_upload_new_version -> doUpload()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fab_button -> doAddNew()
        }
    }

    private fun doAddNew() {
        val intent = Intent()
        intent.setClass(this, AddNewActivity::class.java)
        startActivityForResult(intent, 11)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        drawerLayout!!.closeDrawer(GravityCompat.START)
        when (menuItem.itemId) {
            R.id.action_all -> {
                menuItem.isChecked = true
                mainFragment!!.showAll()
            }
            R.id.action_liucheng -> {
                menuItem.isChecked = true
                mainFragment!!.showAuthor(Constant.Author.LIUCHENG)
            }
            R.id.action_xiaofei -> {
                menuItem.isChecked = true
                mainFragment!!.showAuthor(Constant.Author.XIAOFEI)
            }
            R.id.action_yuri -> {
                menuItem.isChecked = true
                mainFragment!!.showAuthor(Constant.Author.YURI)
            }
            R.id.action_jiesuan -> doStatistics()
            R.id.action_commit -> startCommit()
            R.id.action_about -> {
                val title = "About"
                val message = "Version:" + BuildConfig.VERSION_NAME
                showDialog(title, message)
            }
            R.id.action_check_update -> mMainPresenter!!.checkUpdate(true, mUIHandler)
            R.id.action_encourage -> {
                val intent = Intent()
                intent.setClass(this, PayActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    private fun doStatistics() {
        val localList1 = mainFragment!!.localList
        if (localList1 != null && localList1.isNotEmpty()) {
            Toast.makeText(this, "本地有未提交的数据，请先提交本地数据", Toast.LENGTH_SHORT).show()
            return
        }
        val list = mainFragment!!.getCostList()
        if (list!!.isEmpty()) {
            Toast.makeText(applicationContext, "暂无记录", Toast.LENGTH_LONG).show()
            return
        }
        var totalPay = 0f
        var liuchengPay = 0f
        var xiaofeiPay = 0f
        var yuriPay = 0f
        for (cos in list) {
            totalPay += cos.totalPay
        }

        val startTime = TimeUtil.getDate(list[list.size - 1].createDate)
        val endTime = TimeUtil.getDate(list[0].createDate)

        val sb = StringBuilder()
        sb.append("总共记录条数：" + list.size + "\n")
        sb.append("开始日期:" + startTime + "\n")
        sb.append("结束日期:" + endTime + "\n\n")
        sb.append("总共消费(¥):" + totalPay + "\n")
        sb.append("LiuCheng(¥):" + liuchengPay + "\n")
        sb.append("XiaoFei(¥):" + xiaofeiPay + "\n")
        sb.append("Yuri(¥):" + yuriPay + "\n\n")

        sb.append("(注：结算后本地将不再显示已结算的数据，请确认后再操作)")
        showJieSuanDialog("账单结算", sb.toString(), list)
    }

    private fun showInstallDialog(apkPath: String) {
        Log.d("apkPath:" + apkPath)
        val changeLog = SharedPreferencesUtil.get(applicationContext, "changeLog", "")

        AlertDialog.Builder(this)
                .setMessage("新版本已经后台下载完成。" + "\n"
                        + changeLog)
                .setPositiveButton("立即安装") { dialog, which ->
                    val installIntent = Intent(Intent.ACTION_VIEW)
                    installIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    installIntent.setDataAndType(Uri.parse("file://" + apkPath),
                            "application/vnd.android.package-archive")
                    startActivity(installIntent)
                }
                .setNegativeButton("稍后再说", null)
                .create().show()
    }

    private fun startCommit() {
        val localList = mainFragment!!.localList
        if (localList == null || localList.size == 0) {
            Toast.makeText(this, "本地没有需要提交的数据", Toast.LENGTH_SHORT).show()
        } else {
            val progressDialog = ProgressDialog(this@MainActivity)
            progressDialog.setMessage("提交本地数据中...")
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressDialog.show()
            mMainPresenter!!.doCommit(localList, object : CommitResultListener {
                override fun onCommitSuccess() {
                    mainFragment!!.refresh()
                }

                override fun onCommitFail(errorCode: Int, msg: String) {

                }
            })
        }
    }

    private fun showDialog(title: String, message: String) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create().show()
    }

    private fun showJieSuanDialog(title: String, message: String, list: List<BmobCostYuri>?) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("取消", null)
                .setPositiveButton("结算", DialogInterface.OnClickListener { dialogInterface, i ->
                    progressDialog = ProgressDialog(this@MainActivity)
                    progressDialog!!.setMessage("结算中...")
                    progressDialog!!.setCanceledOnTouchOutside(false)
                    progressDialog!!.setCancelable(false)
                    progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                    progressDialog!!.show()

                    val updateList = ArrayList<BmobObject>()
                    //服务器结算
                    if (list!!.size == 0) {
                        return@OnClickListener
                    }

                    var updateCost: BmobCostYuri
                    for (bmobCost in list) {
                        updateCost = bmobCost
                        updateCost.clear = true
                        updateList.add(updateCost)
                    }

                    doUpdateBatch(updateList, 0, null)
                    //                        if (list.size() > 50) {
                    //                            BmobCost bmobCost;
                    //                            for (int k = 0; k < list.size(); k++) {
                    //                                bmobCost = list.get(k);
                    //                                bmobCost.clear = true;
                    //                                updateList.add(bmobCost);
                    //                                if (updateList.size() >= 50) {
                    //                                    break;
                    //                                }
                    //                            }
                    //                            doUpdateBatch(updateList, 50, list);
                    //                        } else {
                    //                            for (BmobCost bmobCost : list) {
                    //                                bmobCost.clear = true;
                    //                                updateList.add(bmobCost);
                    //                            }
                    //                            doUpdateBatch(updateList, list.size(), null);
                    //                        }
                })
                .create().show()
    }

    /**
     * 更新记录操作
     * @param updateList 需要更新的list
     * @param index 当前更新最后一条记录在源list中的位置
     * @param srcList 源list
     */
    private fun doUpdateBatch(updateList: List<BmobObject>, index: Int, srcList: List<BmobCostYuri>?) {
        Log.d("updateBatch.size:" + updateList.size)
        BmobBatch().updateBatch(updateList).doBatch(object : QueryListListener<BatchResult>() {
            override fun done(list: List<BatchResult>, e: BmobException?) {
                if (e == null) {
                    Log.d("updateBatch success")
                    if (srcList == null || index == srcList.size) {
                        if (progressDialog != null) {
                            progressDialog!!.cancel()
                        }

                        if (mainFragment != null) {
                            mainFragment!!.refresh()
                        }
                    }
                } else {
                    Log.d("updateBatch fail:" + e.message)
                    if (progressDialog != null) {
                        progressDialog!!.cancel()
                    }
                    Toast.makeText(applicationContext, "提交失败:" + e.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mainFragment?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (drawerLayout!!.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout!!.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    override fun onUpdateMoney(detail: String) {
        toolbar!!.subtitle = detail
    }

    override fun showError(message: String) {

    }

    private class UIHandler(activity: MainActivity) : Handler() {
        private val mOuter: WeakReference<MainActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val activity = mOuter.get()
            when (msg.what) {
                MainPresenter.MSG_NO_VERSION_UPDATE -> Toast.makeText(activity, "已经是最新版本了", Toast.LENGTH_SHORT).show()
                MainPresenter.MSG_SHOW_INSTALL_DIALOG -> {
                    val path = msg.obj as String
                    activity!!.showInstallDialog(path)
                }
                MainPresenter.MSG_SHOW_UPDATE_NOTIFICATION -> {
                    val version = msg.data.getString("version")
                    val fileName = msg.data.getString("url")
                    activity!!.showUpdateNotification(version, fileName)
                }
                MainPresenter.MSG_SHOW_INSTALL_NOTIFICATION -> {
                    val version1 = msg.data.getString("version")
                    activity!!.showInstallNotification(version1)
                }
            }
        }
    }

    private fun showInstallNotification(serverVersion: String?) {
        val builder = MMNotificationManager.getInstance(applicationContext).load()
        builder.setNotificationId(Constant.NotificationID.VERSION_UPDAET)
        builder.setContentTitle("新版本已经下载完毕，点击立即安装")
        val cancelBroadcast = ClickPendingIntentBroadCast(
                NotificationReceiver.ACTION_NOTIFICATION_CANCEL)
        val bundle = Bundle()
        bundle.putInt("id", Constant.NotificationID.VERSION_UPDAET)
        cancelBroadcast.setBundle(bundle)

        val installBroadcast = ClickPendingIntentBroadCast(
                NotificationReceiver.ACTION_NOTIFICATION_INSTALL_APP)
        builder.setProfit(NotificationCompat.PRIORITY_MAX)
        builder.addAction(R.mipmap.ic_cancel, "稍后查看", cancelBroadcast)
        builder.addAction(R.mipmap.ic_download, "立即安装", installBroadcast)

        builder.setFullScreenIntent(PendingIntent.getBroadcast(applicationContext, 0,
                Intent(NotificationReceiver.ACTION_NOTIFICATION_CLICK_INTENT), PendingIntent.FLAG_UPDATE_CURRENT), true)

        builder.simpleNotification.build(true)
    }

    private fun showUpdateNotification(serverVersion: String?, url: String?) {
        val builder = MMNotificationManager.getInstance(applicationContext).load()
        builder.setNotificationId(Constant.NotificationID.VERSION_UPDAET)
        builder.setContentTitle("有新版本了:" + serverVersion!!)
        val cancelBroadcast = ClickPendingIntentBroadCast(
                NotificationReceiver.ACTION_NOTIFICATION_CANCEL)
        val bundle = Bundle()
        bundle.putInt("id", Constant.NotificationID.VERSION_UPDAET)
        cancelBroadcast.setBundle(bundle)

        val downloadBroadcast = ClickPendingIntentBroadCast(NotificationReceiver.ACTION_NOTIFICATION_VERSION_UPDATE)
        val bundle1 = Bundle()
        bundle1.putString("versionUrl", url)
        downloadBroadcast.setBundle(bundle1)
        builder.setProfit(NotificationCompat.PRIORITY_MAX)
        builder.addAction(R.mipmap.ic_cancel, "稍后查看", cancelBroadcast)
        builder.addAction(R.mipmap.ic_download, "立即下载", downloadBroadcast)

        builder.setFullScreenIntent(PendingIntent.getBroadcast(applicationContext, 0,
                Intent(NotificationReceiver.ACTION_NOTIFICATION_CLICK_INTENT), PendingIntent.FLAG_UPDATE_CURRENT), true)

        builder.simpleNotification.build(true)
    }

    private fun doUpload() {
        val filePath = "/sdcard/Release_V" + BuildConfig.VERSION_NAME + ".apk"
        if (!File(filePath).exists()) {
            Toast.makeText(applicationContext, filePath + " is not exist", Toast.LENGTH_SHORT).show()
            return
        }
        val progressDialog = ProgressDialog(this@MainActivity)
        progressDialog.setMessage("上传文件中..." + filePath)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.progress = 0
        progressDialog.max = 100
        progressDialog.show()
        mMainPresenter!!.doUpload(filePath, object : Main.OnUploadListener {
            override fun onUploadSuccess() {
                progressDialog.progress = 100
                progressDialog.setMessage("上传完成")
                progressDialog.cancel()
            }

            override fun onUploadProgress(progress: Int) {
                progressDialog.progress = progress
            }

            override fun onUploadFail(msg: String) {
                Log.d("error:" + msg)
                progressDialog.setMessage("上传失败:" + msg)
                progressDialog.cancel()
            }
        })
    }
}

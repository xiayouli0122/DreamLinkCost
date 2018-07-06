package com.yuri.dreamlinkcost.view.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ImageView

import com.yuri.dreamlinkcost.R
import com.yuri.dreamlinkcost.model.Main
import com.yuri.dreamlinkcost.utils.permission.PermissionCallback
import com.yuri.dreamlinkcost.utils.permission.PermissionManager

import java.lang.ref.WeakReference

import tyrantgit.explosionfield.ExplosionField

class SplashActivity2 : AppCompatActivity() {

    private var mExplosionField: ExplosionField? = null

    private var mImageView: ImageView? = null

    private var mHandler: MyHandler? = null

    //companion object 静态对象
    companion object {
        private val MSG_START_ANIM = 0
        private val MSG_END_SPLASH = 1
    }

    private class MyHandler(activity: SplashActivity2) : Handler() {
        private val mOuter: WeakReference<SplashActivity2> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val activity = mOuter.get()
            when (msg.what) {
                MSG_START_ANIM -> activity!!.startAnim()
                MSG_END_SPLASH -> activity!!.finishSplash()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        mExplosionField = ExplosionField.attach2Window(this)

        mImageView = findViewById(R.id.iv_logo)

        mHandler = MyHandler(this)

        if (!PermissionManager.isAndroidM()) {
            //6.0以下直接开始
            startInit()
        } else {
            requestExternalPermission()
        }
    }


    /**
     * android 6.0以后 启动 请求必要权限，暂时只有sdcard的权限是必须的
     */
    private fun requestExternalPermission() {
        if (PermissionManager.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //sdcard权限拿到了，就去看一下个
            requestReadPhoneState()
            return
        }

        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        PermissionManager.askPermission(this, permissions, object : PermissionCallback {
            override fun onPermissionGranted() {
                //允许使用sdcard权限才能开始应用
                requestReadPhoneState()
            }

            override fun onPermissionRefused() {
                requestReadPhoneState()
            }
        })
    }

    private fun requestReadPhoneState() {
        if (PermissionManager.hasPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            startInit()
            return
        }

        val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE)
        PermissionManager.askPermission(this, permissions, object : PermissionCallback {
            override fun onPermissionGranted() {
                //允许使用sdcard权限才能开始应用
                startInit()
            }

            override fun onPermissionRefused() {
                AlertDialog.Builder(this@SplashActivity2)
                        .setMessage("不给权限，不让启动")
                        .setCancelable(false)
                        .setPositiveButton("确定") { _, _ -> finish() }
                        .create().show()
            }
        })
    }

    private fun startInit() {
        mHandler!!.sendEmptyMessageDelayed(MSG_START_ANIM, 500)
    }

    private fun startAnim() {
        if (mExplosionField != null) {
            mExplosionField!!.explode(mImageView!!)
        }

        if (mHandler != null) {
            mHandler!!.sendEmptyMessageDelayed(MSG_END_SPLASH, 1000)
        }
    }

    private fun finishSplash() {
        mHandler!!.removeMessages(MSG_START_ANIM)
        mHandler!!.removeMessages(MSG_END_SPLASH)

        startActivity(Intent(this@SplashActivity2, MainActivity::class.java))

        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()


        mExplosionField!!.clear()
        mExplosionField = null
    }

}

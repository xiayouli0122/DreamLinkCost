package com.yuri.dreamlinkcost.view.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import com.yuri.dreamlinkcost.Constant
import com.yuri.dreamlinkcost.R
import com.yuri.dreamlinkcost.presenter.UserLoginPresenter
import com.yuri.dreamlinkcost.utils.ToastUtil
import com.yuri.dreamlinkcost.view.impl.IUserLoginView
import com.yuri.xlog.Log

import butterknife.ButterKnife
import butterknife.OnClick

class LoginActivity : AppCompatActivity(), View.OnClickListener, IUserLoginView {

    /**
     * 采用MVP模式之后，在View层，也就是Activity界面看不到任何关于数据操作的代码
     */
    private var mPresenter: UserLoginPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        ButterKnife.bind(this)

        mPresenter = UserLoginPresenter(applicationContext, this)
        mPresenter!!.doAutoLogin()
    }

    @OnClick(R.id.tv_liucheng, R.id.tv_xiaofei, R.id.tv_yuri)
    override fun onClick(view: View) {
        Log.d()
        when (view.id) {
            R.id.tv_liucheng -> mPresenter!!.doLogin(Constant.Author.LIUCHENG)
            R.id.tv_xiaofei -> mPresenter!!.doLogin(Constant.Author.XIAOFEI)
            R.id.tv_yuri -> mPresenter!!.doLogin(Constant.Author.YURI)
        }
    }

    override fun onLoginSuccess() {
        goToMainActivity()
    }

    override fun showError(message: String) {
        ToastUtil.showToast(applicationContext, message)
    }

    private fun goToMainActivity() {
        val intent = Intent()
        intent.setClass(this, MainActivity::class.java!!)
        startActivity(intent)
        this.finish()
    }
}

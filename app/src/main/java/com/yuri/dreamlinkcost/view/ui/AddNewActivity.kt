package com.yuri.dreamlinkcost.view.ui

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import com.fourmob.datetimepicker.date.DatePickerDialog
import com.yuri.dreamlinkcost.Constant
import com.yuri.dreamlinkcost.R
import com.yuri.dreamlinkcost.bean.Bmob.BmobCostYuri
import com.yuri.dreamlinkcost.model.CommitResultListener
import com.yuri.dreamlinkcost.presenter.AddNewPresenter
import com.yuri.dreamlinkcost.utils.TimeUtil
import com.yuri.xlog.Log
import kotlinx.android.synthetic.main.activity_addnew.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AddNewActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private var mAuthor: Int = 0

    private var mCalendar: Calendar? = null

    private var mProgressDialog: ProgressDialog? = null

    private var mAddNewPresenter: AddNewPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_addnew)

        setSupportActionBar(toolbar)

        title = "新增数据"

        et_total_price!!.hint = "请输入总价"
        et_total_price!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty()) {
                    et_total_price!!.error = "总价不能为空"
                } else {
                    et_total_price!!.error = null
                }
            }
        })

        mAddNewPresenter = AddNewPresenter(this@AddNewActivity)

        init()

        setResult(Activity.RESULT_CANCELED)

        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setMessage("数据提交中...")
        mProgressDialog!!.setCanceledOnTouchOutside(false)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

        btn_complete.setOnClickListener(this)
        btn_date_picker.setOnClickListener(this)

    }

    private fun init() {
        mAuthor = mAddNewPresenter!!.userId
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val titleArrays = mAddNewPresenter!!.titles
        val operators = resources.getStringArray(R.array.operator_arrays)

        var adapter: ArrayAdapter<*>

        adapter = ArrayAdapter(this, R.layout.simple_spinner_item, titleArrays)
        spinner_title_selector!!.adapter = adapter

        spinner_title_selector!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                Log.d()
                et_title!!.setText(spinner_title_selector!!.selectedItem.toString())
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }

        adapter = ArrayAdapter(this, R.layout.simple_spinner_item, operators)
        spinner_lc!!.prompt = operators[0]
        spinner_xf!!.prompt = operators[0]
        spinner_yuri!!.prompt = operators[0]
        spinner_lc!!.adapter = adapter
        spinner_xf!!.adapter = adapter
        spinner_yuri!!.adapter = adapter

        rb_average!!.setOnCheckedChangeListener { _, _ -> }

        rb_average!!.isChecked = true
        //默认平摊
        rg_pay_way!!.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_average -> item_price_view!!.visibility = View.GONE
                R.id.rb_custom -> item_price_view!!.visibility = View.VISIBLE
            }
        }

        rg_pay_person!!.setOnCheckedChangeListener { _, id ->
            Log.d()
            when (id) {
                R.id.rb_liucheng -> {
                    Log.d("rgPayPerson:rb_liucheng")
                    setPayPerson(Constant.Author.LIUCHENG)
                }
                R.id.rb_xiaofei -> {
                    Log.d("rgPayPerson:rb_xiaofei")
                    setPayPerson(Constant.Author.XIAOFEI)
                }
                R.id.rb_yuri -> {
                    Log.d("rgPayPerson:rb_yuri")
                    setPayPerson(Constant.Author.YURI)
                }
            }
        }

        setPayPerson(mAuthor)

        //默认登录用户为付款人
        when (mAuthor) {
            Constant.Author.LIUCHENG -> rb_liucheng!!.isChecked = true
            Constant.Author.XIAOFEI -> rb_xiaofei!!.isChecked = true
            Constant.Author.YURI -> rb_yuri!!.isChecked = true
        }

        //默认全参与
        cb_liucheng!!.isChecked = true
        cb_xiaofei!!.isChecked = true
        cb_yuri!!.isChecked = true

        tv_date_picker!!.text = "日期:" + TimeUtil.getDate(System.currentTimeMillis())

        cb_liucheng!!.setOnCheckedChangeListener(this)
        cb_xiaofei!!.setOnCheckedChangeListener(this)
        cb_yuri!!.setOnCheckedChangeListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_addnew, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_complete) {
            doComplete()
            return true
        } else if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {
        when (compoundButton.id) {
            R.id.cb_liucheng -> Log.d("liucheng:" + b)
            R.id.cb_xiaofei -> Log.d("cb_xiaofei:" + b)
            R.id.cb_yuri -> Log.d("cb_yuri:" + b)
        }
    }

    private fun setPayPerson(author: Int) {
        when (author) {
            Constant.Author.LIUCHENG -> {
                spinner_lc!!.setSelection(1)
                spinner_xf!!.setSelection(0)
                spinner_yuri!!.setSelection(0)
            }
            Constant.Author.XIAOFEI -> {
                spinner_lc!!.setSelection(0)
                spinner_xf!!.setSelection(1)
                spinner_yuri!!.setSelection(0)
            }
            Constant.Author.YURI -> {
                spinner_lc!!.setSelection(0)
                spinner_xf!!.setSelection(0)
                spinner_yuri!!.setSelection(1)
            }
        }
    }

    private fun doComplete() {
        val titleStr = et_title!!.text.toString().trim { it <= ' ' }
        Log.d("titleStr:" + titleStr)
        if (TextUtils.isEmpty(titleStr)) {
            Toast.makeText(applicationContext, "标题不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        val hasLiuCheng = cb_liucheng!!.isChecked
        val hasXiaoFei = cb_xiaofei!!.isChecked
        val hasYuri = cb_yuri!!.isChecked

        var selectCount = 0
        if (hasLiuCheng) {
            selectCount++
        }

        if (hasXiaoFei) {
            selectCount++
        }

        if (hasYuri) {
            selectCount++
        }

        if (selectCount <= 1) {
            Toast.makeText(applicationContext, "至少选择两个人", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(et_total_price!!.text.toString().trim { it <= ' ' })) {
            Toast.makeText(applicationContext, "总价不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        val cost = BmobCostYuri()
        if (mCalendar != null) {
            cost.createDate = mCalendar!!.timeInMillis
        } else {
            cost.createDate = System.currentTimeMillis()
        }

        cost.totalPay = java.lang.Float.parseFloat(et_total_price!!.text.toString().trim { it <= ' ' })
        cost.title = titleStr
        cost.author = mAuthor

        setResult(Activity.RESULT_OK)
        val author = "Yuri"
        val sb = StringBuilder()
        sb.append("Total(¥)：" + cost.totalPay + "\n")
        sb.append("Editor：" + author + "\n")
        sb.append("CreateDate：" + TimeUtil.getDate(cost.createDate) + "\n")
        AlertDialog.Builder(this)
                .setTitle(cost.title)
                .setMessage(sb.toString())
                .setNegativeButton("取消", null)
                .setPositiveButton("提交") { dialogInterface, which ->
                    if (mProgressDialog != null) {
                        mProgressDialog!!.show()
                    }

                    mAddNewPresenter!!.commit(cost, object : CommitResultListener {
                        override fun onCommitSuccess() {
                            Log.d(cost.toString())
                            if (mProgressDialog != null) {
                                mProgressDialog!!.cancel()
                            }
                            Toast.makeText(applicationContext, "upload success.", Toast.LENGTH_SHORT).show()
                            setResult(Activity.RESULT_OK)
                            this@AddNewActivity.finish()
                        }

                        override fun onCommitFail(errorCode: Int, msg: String) {
                            if (mProgressDialog != null) {
                                mProgressDialog!!.cancel()
                            }
                            Toast.makeText(applicationContext, "upload failure.errorCode:" + errorCode
                                    + ",msg:" + msg, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                .create().show()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setMessage("放弃本次编辑？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定") { _, _ -> this@AddNewActivity.finish() }.create().show()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_date_picker -> {
                Log.d("Yuri", "tvDatePicker")
                val calendar = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog.newInstance({ datePickerDialog, year, month, day ->
                    tv_date_picker!!.text = "Date:" + year + "-" + (month + 1) + "-" + day

                    val monthStr: String
                    val dayStr: String
                    if (month < 9) {
                        monthStr = "0" + (month + 1)
                    } else {
                        monthStr = "" + (month + 1)
                    }

                    if (day < 10) {
                        dayStr = "0" + day
                    } else {
                        dayStr = day.toString() + ""
                    }
                    val dateTime = year.toString() + monthStr + dayStr + "000000"
                    Log.d("dateTime:" + dateTime)
                    mCalendar = Calendar.getInstance()
                    try {
                        mCalendar!!.time = SimpleDateFormat("yyyyMMddhhmmss").parse(dateTime)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false)
                datePickerDialog.setYearRange(2014, 2028)
                datePickerDialog.setCloseOnSingleTapDay(true)
                datePickerDialog.show(supportFragmentManager, "DatePicker")
            }
            R.id.btn_complete -> {
            }
        }
    }
}


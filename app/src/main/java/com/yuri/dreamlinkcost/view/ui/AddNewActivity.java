package com.yuri.dreamlinkcost.view.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.yuri.dreamlinkcost.Constant;
import com.yuri.dreamlinkcost.R;
import com.yuri.dreamlinkcost.bean.table.Cost;
import com.yuri.dreamlinkcost.model.CommitResultListener;
import com.yuri.dreamlinkcost.presenter.AddNewPresenter;
import com.yuri.dreamlinkcost.utils.TimeUtil;
import com.yuri.xlog.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNewActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_date_picker)
    TextView mDateView;
    @BindView(R.id.btn_date_picker)
    Button mDateButton;
    @BindView(R.id.et_title)
    EditText mEtTitle;
    @BindView(R.id.spinner_title_selector)
    Spinner mSpinnerTitleSelector;
    @BindView(R.id.et_total_price)
    EditText mEtTotalPrice;
    @BindView(R.id.rb_average)
    RadioButton mRbAverage;
    @BindView(R.id.rb_custom)
    RadioButton mRbCustom;
    @BindView(R.id.rg_pay_way)
    RadioGroup mRgPayWay;
    @BindView(R.id.cb_liucheng)
    CheckBox mCbLiucheng;
    @BindView(R.id.cb_xiaofei)
    CheckBox mCbXiaofei;
    @BindView(R.id.cb_yuri)
    CheckBox mCbYuri;
    @BindView(R.id.rb_liucheng)
    RadioButton mRbLiucheng;
    @BindView(R.id.rb_xiaofei)
    RadioButton mRbXiaofei;
    @BindView(R.id.rb_yuri)
    RadioButton mRbYuri;
    @BindView(R.id.rg_pay_person)
    RadioGroup mRgPayPerson;
    @BindView(R.id.et_liucheng)
    EditText mEtLiucheng;
    @BindView(R.id.spinner_lc)
    Spinner mSpinnerLc;
    @BindView(R.id.ll_liucheng_price)
    LinearLayout mLlLiuchengPrice;
    @BindView(R.id.et_xiaofei)
    EditText mEtXiaofei;
    @BindView(R.id.spinner_xf)
    Spinner mSpinnerXf;
    @BindView(R.id.ll_xiaofei_price)
    LinearLayout mLlXiaofeiPrice;
    @BindView(R.id.et_yuri)
    EditText mEtYuri;
    @BindView(R.id.spinner_yuri)
    Spinner mSpinnerYuri;
    @BindView(R.id.ll_yuri_price)
    LinearLayout mLlYuriPrice;
    @BindView(R.id.item_price_view)
    LinearLayout mItemPriceView;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.btn_complete)
    Button mBtnComplete;


    private int mAuthor;

    private Calendar mCalendar;

    private ProgressDialog mProgressDialog;

    private AddNewPresenter mAddNewPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_addnew);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        setTitle("新增数据");

        mEtTotalPrice.setHint("请输入总价");
        mEtTotalPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mEtTotalPrice.setError("总价不能为空");
                } else {
                    mEtTotalPrice.setError(null);
                }
            }
        });

        mAddNewPresenter = new AddNewPresenter(getApplicationContext());

        init();

        setResult(RESULT_CANCELED);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Commiting...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    }

    public void init() {
        mAuthor = mAddNewPresenter.getUserId();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] titleArrays = mAddNewPresenter.getTitles();
        String[] operators = getResources().getStringArray(R.array.operator_arrays);

        ArrayAdapter adapter;
        adapter = new ArrayAdapter(getApplicationContext(), R.layout.simple_spinner_item, titleArrays);
        mSpinnerTitleSelector.setAdapter(adapter);

        mSpinnerTitleSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d();
                mEtTitle.setText(mSpinnerTitleSelector.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        adapter = new ArrayAdapter(getApplicationContext(), R.layout.simple_spinner_item, operators);
        mSpinnerLc.setPrompt(operators[0]);
        mSpinnerXf.setPrompt(operators[0]);
        mSpinnerYuri.setPrompt(operators[0]);
        mSpinnerLc.setAdapter(adapter);
        mSpinnerXf.setAdapter(adapter);
        mSpinnerYuri.setAdapter(adapter);

        mRbAverage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            }
        });

        mRbAverage.setChecked(true);
        //默认平摊
        mRgPayWay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_average:
                        mItemPriceView.setVisibility(View.GONE);

                        break;
                    case R.id.rb_custom:
                        mItemPriceView.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        mRgPayPerson.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                Log.d();
                switch (id) {
                    case R.id.rb_liucheng:
                        Log.d("rgPayPerson:rb_liucheng");
                        setPayPerson(Constant.Author.LIUCHENG);
                        break;
                    case R.id.rb_xiaofei:
                        Log.d("rgPayPerson:rb_xiaofei");
                        setPayPerson(Constant.Author.XIAOFEI);
                        break;
                    case R.id.rb_yuri:
                        Log.d("rgPayPerson:rb_yuri");
                        setPayPerson(Constant.Author.YURI);
                        break;
                }
            }
        });

        setPayPerson(mAuthor);

        //默认登录用户为付款人
        switch (mAuthor) {
            case Constant.Author.LIUCHENG:
                mRbLiucheng.setChecked(true);
                break;
            case Constant.Author.XIAOFEI:
                mRbXiaofei.setChecked(true);
                break;
            case Constant.Author.YURI:
                mRbYuri.setChecked(true);
                break;
        }

        //默认全参与
        mCbLiucheng.setChecked(true);
        mCbXiaofei.setChecked(true);
        mCbYuri.setChecked(true);

        mDateView.setText("Date:" + TimeUtil.getDate(System.currentTimeMillis()));

        mCbLiucheng.setOnCheckedChangeListener(this);
        mCbXiaofei.setOnCheckedChangeListener(this);
        mCbYuri.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_addnew, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_complete) {
            doComplete();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cb_liucheng:
                Log.d("liucheng:" + b);
                break;
            case R.id.cb_xiaofei:
                Log.d("cb_xiaofei:" + b);
                break;
            case R.id.cb_yuri:
                Log.d("cb_yuri:" + b);
                break;
        }
    }

    private void setPayPerson(int author) {
        switch (author) {
            case Constant.Author.LIUCHENG:
                mSpinnerLc.setSelection(1);
                mSpinnerXf.setSelection(0);
                mSpinnerYuri.setSelection(0);
                break;
            case Constant.Author.XIAOFEI:
                mSpinnerLc.setSelection(0);
                mSpinnerXf.setSelection(1);
                mSpinnerYuri.setSelection(0);
                break;
            case Constant.Author.YURI:
                mSpinnerLc.setSelection(0);
                mSpinnerXf.setSelection(0);
                mSpinnerYuri.setSelection(1);
                break;
        }
    }

    public void doComplete() {
        String titleStr = mEtTitle.getText().toString().trim();
        Log.d("titleStr:" + titleStr);
        if (TextUtils.isEmpty(titleStr)) {
            Toast.makeText(getApplicationContext(), "标题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasLiuCheng = mCbLiucheng.isChecked();
        boolean hasXiaoFei = mCbXiaofei.isChecked();
        boolean hasYuri = mCbYuri.isChecked();

        int selectCount = 0;
        if (hasLiuCheng) {
            selectCount++;
        }

        if (hasXiaoFei) {
            selectCount++;
        }

        if (hasYuri) {
            selectCount++;
        }

        if (selectCount <= 1) {
            Toast.makeText(getApplicationContext(), "至少选择两个人", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mEtTotalPrice.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "总价不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isAverage = mRbAverage.isChecked();
        Log.d("isAverage:" + isAverage);
        if (!isAverage) {
            if (hasLiuCheng && TextUtils.isEmpty(mEtLiucheng.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), "LiuCheng Pay cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (hasXiaoFei && TextUtils.isEmpty(mEtXiaofei.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), "XiaoFei Pay cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (hasYuri && TextUtils.isEmpty(mEtYuri.toString().trim())) {
                Toast.makeText(getApplicationContext(), "Yuri Pay cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        final Cost cost = new Cost();
        if (mCalendar != null) {
            cost.createDate = mCalendar.getTimeInMillis();
        } else {
            cost.createDate = System.currentTimeMillis();
        }

        cost.totalPay = Float.parseFloat(mEtTotalPrice.getText().toString().trim());
        cost.title = titleStr;
        cost.author = mAuthor;

        if (isAverage) {
            float bili1;
            float bili2;
            if (selectCount == 2) {
                bili1 = (float) (1.0 / 2);
                bili2 = (float) (1.0 / 2);
            } else {
                bili1 = (float) (2.0 / 3);
                bili2 = (float) (1.0 / 3);
            }
            Log.d("bili1:" + bili1 + ",bili2:" + bili2);
            switch (mRgPayPerson.getCheckedRadioButtonId()) {
                case R.id.rb_liucheng:
                    cost.payLC = cost.totalPay * bili1;
                    cost.payXF = hasXiaoFei ? -cost.totalPay * bili2 : 0;
                    cost.payYuri = hasYuri ? -cost.totalPay * bili2 : 0;
                    break;
                case R.id.rb_xiaofei:
                    cost.payXF = cost.totalPay * bili1;
                    cost.payLC = hasLiuCheng ? -cost.totalPay * bili2 : 0;
                    cost.payYuri = hasYuri ? -cost.totalPay * bili2 : 0;
                    break;
                case R.id.rb_yuri:
                    cost.payYuri = cost.totalPay * bili1;
                    cost.payLC = hasLiuCheng ? -cost.totalPay * bili2 : 0;
                    cost.payXF = hasXiaoFei ? -cost.totalPay * bili2 : 0;
                    break;
            }
        } else {
            float payLc = 0;
            float payXf = 0;
            float payYuri = 0;
            if (hasLiuCheng) {
                payLc = Float.parseFloat(mEtLiucheng.getText().toString().trim());
            }

            if (hasXiaoFei) {
                payXf = Float.parseFloat(mEtXiaofei.getText().toString().trim());
            }

            if (hasYuri) {
                payYuri = Float.parseFloat(mEtYuri.getText().toString().trim());
            }

            switch (mRgPayPerson.getCheckedRadioButtonId()) {
                case R.id.rb_liucheng:
                    cost.payLC = payLc;
                    cost.payXF = -payXf;
                    cost.payYuri = -payYuri;
                    break;
                case R.id.rb_xiaofei:
                    cost.payXF = payXf;
                    cost.payLC = -payLc;
                    cost.payYuri = -payYuri;
                    break;
                case R.id.rb_yuri:
                    cost.payYuri = payYuri;
                    cost.payLC = -payLc;
                    cost.payXF = -payXf;
                    break;
            }
        }

        if (!isAverage && (cost.payLC + cost.payXF + cost.payYuri) != 0) {
            Toast.makeText(this, "Error.LC:" + cost.payLC + ",XF:" + cost.payXF + ",Yuri:" + cost.payYuri, Toast.LENGTH_SHORT).show();
        } else {
            setResult(RESULT_OK);
            String author;
            if (cost.author == Constant.Author.LIUCHENG) {
                author = "LiuCheng";
            } else if (cost.author == Constant.Author.XIAOFEI) {
                author = "XiaoFei";
            } else if (cost.author == Constant.Author.YURI) {
                author = "Yuri";
            } else {
                author = "UNKNOW";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Total(¥)：" + cost.totalPay + "\n");
            sb.append("LiuCheng(¥)：" + cost.payLC + "\n");
            sb.append("XiaoFei(¥)：" + cost.payXF + "\n");
            sb.append("Yuri(¥)：" + cost.payYuri + "\n\n\n");
            sb.append("Editor：" + author + "\n");
            sb.append("CreateDate：" + TimeUtil.getDate(cost.createDate) + "\n");
            new AlertDialog.Builder(this)
                    .setTitle(cost.title)
                    .setMessage(sb.toString())
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Commit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            if (mProgressDialog != null) {
                                mProgressDialog.show();
                            }

                            mAddNewPresenter.commit(cost, new CommitResultListener() {
                                @Override
                                public void onCommitSuccess() {
                                    Log.d(cost.toString());
                                    if (mProgressDialog != null) {
                                        mProgressDialog.cancel();
                                    }
                                    Toast.makeText(getApplicationContext(), "upload success.", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    AddNewActivity.this.finish();
                                }

                                @Override
                                public void onCommitFail(int errorCode, String msg) {
                                    if (mProgressDialog != null) {
                                        mProgressDialog.cancel();
                                    }
                                    setResult(RESULT_OK);
                                    Toast.makeText(getApplicationContext(), "upload failure.errorCode:" + errorCode
                                            + ",msg:" + msg, Toast.LENGTH_SHORT).show();
                                    AddNewActivity.this.finish();
                                }
                            });
                        }
                    })
                    .create().show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("放弃本次编辑？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AddNewActivity.this.finish();
                    }
                }).create().show();
    }

    @OnClick({R.id.btn_date_picker, R.id.btn_complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_date_picker:
                Log.d("Yuri", "tvDatePicker");
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

                        mDateView.setText("Date:" + year + "-" + (month + 1) + "-" + day);

                        String monthStr;
                        String dayStr;
                        if (month < 9) {
                            monthStr = "0" + (month + 1);
                        } else {
                            monthStr = "" + (month + 1);
                        }

                        if (day < 10) {
                            dayStr = "0" + day;
                        } else {
                            dayStr = day + "";
                        }
                        String dateTime = year + monthStr + dayStr + "000000";
                        Log.d("dateTime:" + dateTime);
                        mCalendar = Calendar.getInstance();
                        try {
                            mCalendar.setTime(new SimpleDateFormat("yyyyMMddhhmmss").parse(dateTime));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
                datePickerDialog.setYearRange(2014, 2028);
                datePickerDialog.setCloseOnSingleTapDay(true);
                datePickerDialog.show(getSupportFragmentManager(), "DatePicker");
                break;
            case R.id.btn_complete:
                break;
        }
    }
}


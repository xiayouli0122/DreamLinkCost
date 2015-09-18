package com.yuri.dreamlinkcost;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.yuri.dreamlinkcost.Bmob.BmobCost;
import com.yuri.dreamlinkcost.Bmob.BmobTitle;
import com.yuri.dreamlinkcost.binder.AddNewModel;
import com.yuri.dreamlinkcost.databinding.AddNewerBinder;
import com.yuri.dreamlinkcost.log.Log;
import com.yuri.dreamlinkcost.model.Cost;
import com.yuri.dreamlinkcost.model.Title;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.listener.SaveListener;

public class AddNewActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private SharedPreferences mSharedPrefences;
    private int mAuthor;

    private Calendar mCalendar;

    private ProgressDialog mProgressDialog;

    private AddNewerBinder mBinding;
    private AddNewModel mAddNewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_addnew);
        mBinding.setToolbarTitle("新增数据");
        mBinding.setToolbarSubTitle("手动输入数据");

        mAddNewModel = new AddNewModel();
        mBinding.setAddNewModel(mAddNewModel);

        init();

        setResult(RESULT_CANCELED);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Commiting...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    }

    public void init(){
        mSharedPrefences = getSharedPreferences(Constant.SHARED_NAME, MODE_PRIVATE);
        mAuthor = mSharedPrefences.getInt(Constant.Extra.KEY_LOGIN, Constant.Author.YURI);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<Title> titles = new Select().from(Title.class).execute();
        String[] titleArrays ;
        if (titles == null) {
            titleArrays = getResources().getStringArray(R.array.title_arrays);
        } else {
            titleArrays = new String[titles.size()];
            for (int i = 0; i < titles.size(); i++) {
                titleArrays[i] = titles.get(i).mTitle;
            }
        }
        String[] operators = getResources().getStringArray(R.array.operator_arrays);

        ArrayAdapter adapter;
        adapter= new ArrayAdapter(getApplicationContext(), R.layout.simple_spinner_item, titleArrays);
        mBinding.spinnerTitleSelector.setAdapter(adapter);

        mBinding.spinnerTitleSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d();
//                etTitle.setText(spinnerTitleSelector.getSelectedItem() + "");
                mAddNewModel.title = mBinding.spinnerTitleSelector.getSelectedItem() + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        adapter= new ArrayAdapter(getApplicationContext(), R.layout.simple_spinner_item, operators);
        mBinding.spinnerLc.setPrompt(operators[0]);
        mBinding.spinnerXf.setPrompt(operators[0]);
        mBinding.spinnerYuri.setPrompt(operators[0]);

        mBinding.spinnerLc.setAdapter(adapter);
        mBinding.spinnerXf.setAdapter(adapter);
        mBinding.spinnerYuri.setAdapter(adapter);

        mBinding.rbAverage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mAddNewModel.isAverageUserChecked.set(b);
            }
        });

        mBinding.rgPayPerson.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                Log.d();
                switch (id) {
                    case R.id.rb_liucheng:
                        Log.d("rgPayPerson:rb_liucheng");
                        mAddNewModel.whichOnePay.set(0);
                        mBinding.spinnerLc.setSelection(1);
                        mBinding.spinnerXf.setSelection(0);
                        mBinding.spinnerYuri.setSelection(0);
                        break;
                    case R.id.rb_xiaofei:
                        Log.d("rgPayPerson:rb_xiaofei");
                        mAddNewModel.whichOnePay.set(1);
                        mBinding.spinnerLc.setSelection(0);
                        mBinding.spinnerXf.setSelection(1);
                        mBinding.spinnerYuri.setSelection(0);
                        break;
                    case R.id.rb_yuri:
                        Log.d("rgPayPerson:rb_yuri");
                        mAddNewModel.whichOnePay.set(2);
                        mBinding.spinnerLc.setSelection(0);
                        mBinding.spinnerXf.setSelection(0);
                        mBinding.spinnerYuri.setSelection(1);
                        break;
                }
            }
        });

        mBinding.tvDatePicker.setText("Date:" + Utils.getDate(System.currentTimeMillis()));
        mBinding.btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Yuri", "tvDatePicker");
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        mBinding.tvDatePicker.setText("Date:" + year + "-" + (month + 1) + "-" + day);
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
                        Log.d("Yuri", "dateTime:" + dateTime);
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
            }
        });

        mBinding.cbLiucheng.setOnCheckedChangeListener(this);
        mBinding.cbXiaofei.setOnCheckedChangeListener(this);
        mBinding.cbYuri.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.cb_liucheng:
                Log.d("liucheng:" + b);
                mAddNewModel.isLiuChengIn.set(b);
                break;
            case R.id.cb_xiaofei:
                Log.d("cb_xiaofei:" + b);
                mAddNewModel.isXiaoFeiIn.set(b);
                break;
            case R.id.cb_yuri:
                Log.d("cb_yuri:" + b);
                mAddNewModel.isYuriIn.set(b);
                break;
        }
    }

    public void doComplete() {
        String titleStr = mAddNewModel.title;
        Log.d("titleStr:" + titleStr);
        if (TextUtils.isEmpty(titleStr)) {
            Toast.makeText(getApplicationContext(), "标题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        saveNewTitle(titleStr);

        int selectCount = 0;
        if (mAddNewModel.isLiuChengIn.get()) {
            selectCount ++;
        }

        if (mAddNewModel.isXiaoFeiIn.get()) {
            selectCount ++;
        }

        if (mAddNewModel.isYuriIn.get()) {
            selectCount ++;
        }

        if (selectCount <= 1) {
            Toast.makeText(getApplicationContext(), "至少选择两个人", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mAddNewModel.totalPrice)) {
            Toast.makeText(getApplicationContext(), "TotalPay cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mAddNewModel.whichOnePay.get() == -1) {
            Toast.makeText(getApplicationContext(), "You must confirm who pay.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasLiuCheng = mAddNewModel.isLiuChengIn.get();
        boolean hasXiaoFei = mAddNewModel.isXiaoFeiIn.get();
        boolean hasYuri = mAddNewModel.isYuriIn.get();

        if (!mAddNewModel.isAverageUserChecked.get()) {
            if (hasLiuCheng && TextUtils.isEmpty(mAddNewModel.liucheng)) {
                Toast.makeText(getApplicationContext(), "LiuCheng Pay cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (hasXiaoFei && TextUtils.isEmpty(mAddNewModel.xiaofei)) {
                Toast.makeText(getApplicationContext(), "XiaoFei Pay cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (hasYuri && TextUtils.isEmpty(mAddNewModel.yuri)) {
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

        cost.totalPay = Float.parseFloat(mAddNewModel.totalPrice);
        cost.title = mAddNewModel.title;
        cost.author = mAuthor;
        if (mAddNewModel.isAverageUserChecked.get()) {
            if (hasLiuCheng && hasXiaoFei && hasYuri) {
                switch (mAddNewModel.whichOnePay.get()) {
                    case 0:
                        cost.payLC = cost.totalPay * (float) (2.0 / 3.0);
                        cost.payXF = -cost.totalPay / 3;
                        cost.payYuri = -cost.totalPay / 3;
                        break;
                    case 1:
                        cost.payXF = cost.totalPay * (float) (2.0 / 3.0);
                        cost.payLC = -cost.totalPay / 3;
                        cost.payYuri = -cost.totalPay / 3;
                        break;
                    case 2:
                        cost.payYuri = cost.totalPay * (float) (2.0 / 3.0);
                        cost.payXF = -cost.totalPay / 3;
                        cost.payLC = -cost.totalPay / 3;
                        break;
                }
            } else if (hasLiuCheng && hasXiaoFei && !hasYuri) {
                switch (mAddNewModel.whichOnePay.get()) {
                    case 0:
                        cost.payLC = cost.totalPay / 2;
                        cost.payXF = -cost.totalPay / 2;
                        cost.payYuri = 0;
                        break;
                    case 1:
                        cost.payXF = cost.totalPay / 2;
                        cost.payLC = -cost.totalPay / 2;
                        cost.payYuri = 0;
                        break;
                }
            } else if (!hasLiuCheng && hasXiaoFei && hasYuri) {
                switch (mAddNewModel.whichOnePay.get()) {
                    case 2:
                        cost.payYuri = cost.totalPay / 2;
                        cost.payXF = -cost.totalPay / 2;
                        cost.payLC = 0;
                        break;
                    case 1:
                        cost.payXF = cost.totalPay / 2;
                        cost.payYuri = -cost.totalPay / 2;
                        cost.payLC = 0;
                        break;
                }
            } else if (hasLiuCheng && !hasXiaoFei && hasYuri) {
                switch (mAddNewModel.whichOnePay.get()) {
                    case 1:
                        cost.payYuri = cost.totalPay / 2;
                        cost.payLC = -cost.totalPay / 2;
                        cost.payXF = 0;
                        break;
                    case 0:
                        cost.payLC = cost.totalPay / 2;
                        cost.payYuri = -cost.totalPay / 2;
                        cost.payXF = 0;
                        break;
                }
            }

        } else {
            float payLc = 0;
            float payXf = 0;
            float payYuri = 0;
            if (hasLiuCheng) {
                payLc = Float.parseFloat(mAddNewModel.liucheng);
            }

            if (hasXiaoFei) {
                payXf = Float.parseFloat(mAddNewModel.xiaofei);
            }

            if (hasYuri) {
                payYuri = Float.parseFloat(mAddNewModel.yuri);
            }

            switch (mAddNewModel.whichOnePay.get()) {
                case 0:
                    cost.payLC = payLc;
                    cost.payXF = -payXf;
                    cost.payYuri = -payYuri;
                    break;
                case 1:
                    cost.payXF = payXf;
                    cost.payLC = -payLc;
                    cost.payYuri = -payYuri;
                    break;
                case 2:
                    cost.payYuri = payYuri;
                    cost.payLC = -payLc;
                    cost.payXF = -payXf;
                    break;
            }
        }
        if (!mAddNewModel.isAverageUserChecked.get() && (cost.payLC + cost.payXF + cost.payYuri) != 0) {
            Toast.makeText(this, "Error.LC:" + cost.payLC + ",XF:" + cost.payXF + ",Yuri:" + cost.payYuri, Toast.LENGTH_SHORT).show();
        } else {
            setResult(RESULT_OK);
            String author = "";
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
            sb.append("CreateDate：" + Utils.getDate(cost.createDate) + "\n");
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
                            final BmobCost bmobCost = cost.getCostBean();
                            bmobCost.save(getApplicationContext(), new SaveListener() {
                                        @Override
                                        public void onSuccess() {
                                            if (mProgressDialog != null) {
                                                mProgressDialog.cancel();
                                            }
                                            Toast.makeText(getApplicationContext(), "upload success.", Toast.LENGTH_SHORT).show();
                                            setResult(RESULT_OK);
                                            Log.d("Yuri", cost.toString());
                                            AddNewActivity.this.finish();
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            if (mProgressDialog != null) {
                                                mProgressDialog.cancel();
                                            }
                                            cost.save();
                                            setResult(RESULT_OK);
                                            Toast.makeText(getApplicationContext(), "upload failure.errorCode:" + i
                                                    + ",msg:" + s, Toast.LENGTH_SHORT).show();
                                            AddNewActivity.this.finish();
                                        }
                                    }
                            );
                        }
                    })
                    .create().show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_addnew, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_complete) {
            doComplete();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void saveNewTitle(String titleStr) {
        Title title = new Select().from(Title.class).where("title=?", titleStr).executeSingle();
        if (title == null) {
            final  Title title2 = new Title();
            title2.mTitle = titleStr;
            title2.mHasCommited = false;
            title2.save();

            BmobTitle bmobTitle = title2.getBmobTitle();
            bmobTitle.save(getApplicationContext(), new SaveListener() {
                @Override
                public void onSuccess() {
                    title2.mHasCommited = true;
                    title2.save();
                }

                @Override
                public void onFailure(int i, String s) {

                }
            });
        }
    }
}

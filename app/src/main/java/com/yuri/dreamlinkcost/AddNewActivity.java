package com.yuri.dreamlinkcost;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.yuri.dreamlinkcost.Bmob.BmobCost;
import com.yuri.dreamlinkcost.model.Cost;
import com.yuri.dreamlinkcost.model.Title;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import cn.bmob.v3.listener.SaveListener;

@EActivity(R.layout.activity_addnew)
public class AddNewActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    @ViewById
    TextView tvDatePicker;
    @ViewById
    Button btnDatePicker;
    @ViewById
    EditText etTitle;
    @ViewById
    Spinner spinnerTitleSelector;
    @ViewById
    EditText etTotalPrice;
    @ViewById
    EditText etLiucheng;
    @ViewById
    Spinner spinnerLc;
    @ViewById
    EditText etXiaofei;
    @ViewById
    Spinner spinnerXf;
    @ViewById
    EditText etYuri;
    @ViewById
    Spinner spinnerYuri;
    @ViewById
    LinearLayout itemPriceView;
    @ViewById(R.id.rg_pay_way)
    RadioGroup mPayWayRG;
    @ViewById(R.id.rg_pay_person)
    RadioGroup mPayPersonRG;
    @ViewById(R.id.rb_liucheng)
    RadioButton mLiuChengRB;
    @ViewById(R.id.rb_xiaofei)
    RadioButton mXiaoFeiRB;
    @ViewById(R.id.rb_yuri)
    RadioButton mYuriRB;

    @ViewById(R.id.cb_liucheng)
    CheckBox mLiuChengCB;
    @ViewById(R.id.cb_xiaofei)
    CheckBox mXiaoFeiCB;
    @ViewById(R.id.cb_yuri)
    CheckBox mYuriCB;

    @ViewById(R.id.ll_liucheng_price)
    View mLiuChengPriceView;
    @ViewById(R.id.ll_xiaofei_price)
    View mXiaoFeiPriceView;
    @ViewById(R.id.ll_yuri_price)
    View mYuriPriceView;

    private SharedPreferences mSharedPrefences;
    private int mAuthor;

    private int mPayWay = -1;
    private static final int PAY_WAY_AVERAGE = 0;
    private static final int PAY_WAY_CUSTOM = 1;

    private PayPerson mPayPerson;

    private SparseBooleanArray mCheckedArray;

    private static  final int LIUCHENG = 0;
    public static final int XIAOFEI = 1;
    public static final int  YURI =  2;

    private Calendar mCalendar;

    private ProgressDialog mProgressDialog;

    enum PayPerson{
        LiuCheng, XiaoFei, Yuri
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //可省略
        //setContentView(R.layout.activity_addnew);
        //tvDatePicker.setText("");  报错，空指针异常
        //因为在onCreate()被调用的时候，@ViewById还没有被set，也就是都为null
        //所以如果你要对组件进行一定的初始化，那么你要用@AfterViews注解
        setResult(RESULT_CANCELED);

        mCheckedArray = new SparseBooleanArray(3);
        mCheckedArray.put(LIUCHENG, true);
        mCheckedArray.put(XIAOFEI, true);
        mCheckedArray.put(YURI, true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Commiting...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    }

    @AfterViews
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
        Log.d("Yuri", "spinnerTitleSelector:" + spinnerTitleSelector);
        spinnerTitleSelector.setAdapter(adapter);

        spinnerTitleSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                etTitle.setText(spinnerTitleSelector.getSelectedItem() + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        adapter= new ArrayAdapter(getApplicationContext(), R.layout.simple_spinner_item, operators);
        spinnerLc.setPrompt(operators[0]);
        spinnerXf.setPrompt(operators[0]);
        spinnerYuri.setPrompt(operators[0]);

        spinnerLc.setAdapter(adapter);
        spinnerXf.setAdapter(adapter);
        spinnerYuri.setAdapter(adapter);

        itemPriceView.setVisibility(View.GONE);
        mPayWayRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.rb_average) {
                    itemPriceView.setVisibility(View.GONE);
                    mPayWay = PAY_WAY_AVERAGE;
                } else {
                    itemPriceView.setVisibility(View.VISIBLE);
                    mPayWay = PAY_WAY_CUSTOM;
                    mLiuChengPriceView.setVisibility(mCheckedArray.get(LIUCHENG) ? View.VISIBLE : View.GONE);
                    mXiaoFeiPriceView.setVisibility(mCheckedArray.get(XIAOFEI) ? View.VISIBLE : View.GONE);
                    mYuriPriceView.setVisibility(mCheckedArray.get(YURI) ? View.VISIBLE : View.GONE);
                }
            }
        });
        mPayPersonRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.rb_liucheng:
                        spinnerLc.setSelection(1);
                        spinnerXf.setSelection(0);
                        spinnerYuri.setSelection(0);
                        mPayPerson = PayPerson.LiuCheng;
                        break;
                    case R.id.rb_xiaofei:
                        spinnerLc.setSelection(0);
                        spinnerXf.setSelection(1);
                        spinnerYuri.setSelection(0);
                        mPayPerson = PayPerson.XiaoFei;
                        break;
                    case R.id.rb_yuri:
                        spinnerLc.setSelection(0);
                        spinnerXf.setSelection(0);
                        spinnerYuri.setSelection(1);
                        mPayPerson = PayPerson.Yuri;
                        break;
                }
            }
        });

        tvDatePicker.setText("Date:" + Utils.getDate(System.currentTimeMillis()));
        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Yuri", "tvDatePicker");
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
                        tvDatePicker.setText("Date:" + year + "-" + (month+1) + "-" + day);
                        String monthStr = "00";
                        String dayStr = "00";
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
                        String dateTime = year + monthStr + dayStr+ "000000";
                        Log.d("Yuri", "dateTime:" + dateTime);
                        mCalendar= Calendar.getInstance();
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

        mLiuChengCB.setChecked(true);
        mXiaoFeiCB.setChecked(true);
        mYuriCB.setChecked(true);
        mLiuChengCB.setOnCheckedChangeListener(this);
        mXiaoFeiCB.setOnCheckedChangeListener(this);
        mYuriCB.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        Log.d("Yuri", "id:" + compoundButton.getId() + ",checked:" + b);
        switch (compoundButton.getId()) {
            case R.id.cb_liucheng:
                mCheckedArray.put(LIUCHENG, b);
                break;
            case R.id.cb_xiaofei:
                mCheckedArray.put(XIAOFEI, b);
                break;
            case R.id.cb_yuri:
                mCheckedArray.put(YURI, b);
                break;
        }
        Log.d("Yuri", "aaaaaaaaaaa");
        doSelectWhich();
    }

    private void doSelectWhich() {
        Log.d("Yuri", "doSelectWhich");
        mLiuChengRB.setVisibility(mCheckedArray.get(LIUCHENG) ? View.VISIBLE : View.GONE);
        mXiaoFeiRB.setVisibility(mCheckedArray.get(XIAOFEI) ? View.VISIBLE : View.GONE);
        mYuriRB.setVisibility(mCheckedArray.get(YURI) ? View.VISIBLE : View.GONE);
        if (mPayWay == PAY_WAY_CUSTOM) {
            mLiuChengPriceView.setVisibility(mCheckedArray.get(LIUCHENG) ? View.VISIBLE : View.GONE);
            mXiaoFeiPriceView.setVisibility(mCheckedArray.get(XIAOFEI) ? View.VISIBLE : View.GONE);
            mYuriPriceView.setVisibility(mCheckedArray.get(YURI) ? View.VISIBLE : View.GONE);
        }
    }

    public void doComplete() {
        String titleStr = etTitle.getText().toString().trim();
        if (!TextUtils.isEmpty(titleStr)) {
            Title title = new Select().from(Title.class).where("title=?", titleStr).executeSingle();
            if (title == null) {
                title = new Title();
                title.mTitle = titleStr;
                title.save();
            }
        }
        int selectCount = 0;
        for (int i = 0; i < mCheckedArray.size(); i++) {
            if (mCheckedArray.valueAt(i)) {
                selectCount++;
            }
        }

        if (selectCount <= 1) {
            Toast.makeText(getApplicationContext(), "至少选择两个人", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(etTitle.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "Title cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(etTotalPrice.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "TotalPay cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mPayWay == -1) {
            Toast.makeText(getApplicationContext(), "You must confirm how pay.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mPayPerson == null) {
            Toast.makeText(getApplicationContext(), "You must confirm who pay.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasLiuCheng = mCheckedArray.get(LIUCHENG);
        boolean hasXiaoFei = mCheckedArray.get(XIAOFEI);
        boolean hasYuri = mCheckedArray.get(YURI);

        if (mPayWay == PAY_WAY_CUSTOM) {
            mLiuChengPriceView.setVisibility(mCheckedArray.get(LIUCHENG) ? View.VISIBLE : View.GONE);
            mXiaoFeiPriceView.setVisibility(mCheckedArray.get(XIAOFEI) ? View.VISIBLE : View.GONE);
            mYuriPriceView.setVisibility(mCheckedArray.get(YURI) ? View.VISIBLE : View.GONE);

            if (hasLiuCheng && TextUtils.isEmpty(etLiucheng.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), "LiuCheng Pay cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (hasXiaoFei && TextUtils.isEmpty(etXiaofei.getText().toString().trim())) {
                Toast.makeText(getApplicationContext(), "XiaoFei Pay cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (hasYuri && TextUtils.isEmpty(etYuri.getText().toString().trim())) {
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
        cost.totalPay = Float.parseFloat(etTotalPrice.getText().toString());
        cost.title = etTitle.getText().toString().trim();
        cost.author = mAuthor;
        if (mPayWay == PAY_WAY_AVERAGE) {
            if (hasLiuCheng && hasXiaoFei && hasYuri) {
                switch (mPayPerson) {
                    case LiuCheng:
                        cost.payLC = cost.totalPay * (float) (2.0 / 3.0);
                        cost.payXF = -cost.totalPay / 3;
                        cost.payYuri = -cost.totalPay / 3;
                        break;
                    case XiaoFei:
                        cost.payXF = cost.totalPay * (float) (2.0 / 3.0);
                        cost.payLC = -cost.totalPay / 3;
                        cost.payYuri = -cost.totalPay / 3;
                        break;
                    case Yuri:
                        cost.payYuri = cost.totalPay * (float) (2.0 / 3.0);
                        cost.payXF = -cost.totalPay / 3;
                        cost.payLC = -cost.totalPay / 3;
                        break;
                }
            } else if (hasLiuCheng && hasXiaoFei && !hasYuri) {
                switch (mPayPerson) {
                    case LiuCheng:
                        cost.payLC = cost.totalPay / 2;
                        cost.payXF = -cost.totalPay / 2;
                        cost.payYuri = 0;
                        break;
                    case XiaoFei:
                        cost.payXF = cost.totalPay / 2;
                        cost.payLC = -cost.totalPay / 2;
                        cost.payYuri = 0;
                        break;
                }
            } else if (!hasLiuCheng && hasXiaoFei && hasYuri) {
                switch (mPayPerson) {
                    case Yuri:
                        cost.payYuri = cost.totalPay / 2;
                        cost.payXF = -cost.totalPay / 2;
                        cost.payLC = 0;
                        break;
                    case XiaoFei:
                        cost.payXF = cost.totalPay / 2;
                        cost.payYuri = -cost.totalPay / 2;
                        cost.payLC = 0;
                        break;
                }
            } else if (hasLiuCheng && !hasXiaoFei && hasYuri) {
                switch (mPayPerson) {
                    case Yuri:
                        cost.payYuri = cost.totalPay / 2;
                        cost.payLC = -cost.totalPay / 2;
                        cost.payXF = 0;
                        break;
                    case LiuCheng:
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
                payLc = Float.parseFloat(etLiucheng.getText().toString().trim());
            }

            if (hasXiaoFei) {
                payXf = Float.parseFloat(etXiaofei.getText().toString().trim());
            }

            if (hasYuri) {
                payYuri = Float.parseFloat(etYuri.getText().toString().trim());
            }

            switch (mPayPerson) {
                case LiuCheng:
                    cost.payLC = payLc;
                    cost.payXF = -payXf;
                    cost.payYuri = -payYuri;
                    break;
                case XiaoFei:
                    cost.payXF = payXf;
                    cost.payLC = -payLc;
                    cost.payYuri = -payYuri;
                    break;
                case Yuri:
                    cost.payYuri = payYuri;
                    cost.payLC = -payLc;
                    cost.payXF = -payXf;
                    break;
            }
        }
        if (mPayWay == PAY_WAY_CUSTOM && (cost.payLC + cost.payXF + cost.payYuri) != 0) {
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
                                ;
                            }
                            final BmobCost bmobCost = cost.getCostBean();
                            bmobCost.save(getApplicationContext(), new SaveListener() {
                                        @Override
                                        public void onSuccess() {
                                            if (mProgressDialog != null) {
                                                mProgressDialog.cancel();
                                            }
                                            Toast.makeText(getApplicationContext(), "upload success.", Toast.LENGTH_SHORT).show();
                                            cost.status = Constant.STATUS_COMMIT_SUCCESS;
                                            cost.objectId = bmobCost.getObjectId();
                                            cost.save();
                                            Log.d("Yuri", cost.toString());
                                            AddNewActivity.this.finish();
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            if (mProgressDialog != null) {
                                                mProgressDialog.cancel();
                                            }
                                            cost.save();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addnew, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_complete) {
            doComplete();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

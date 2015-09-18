package com.yuri.dreamlinkcost;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yuri.dreamlinkcost.databinding.ActivityDataBindTestBinding;

public class DataBindTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_data_bind_test);
        ActivityDataBindTestBinding bindTestBinding = DataBindingUtil.setContentView(this, R.layout.activity_data_bind_test);
        bindTestBinding.setTest("第一条databind测试");
    }

}

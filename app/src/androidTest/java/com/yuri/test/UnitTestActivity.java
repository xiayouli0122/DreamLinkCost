package com.yuri.test;

import android.test.ActivityInstrumentationTestCase2;

import com.yuri.dreamlinkcost.MainActivity;

/**
 * Created by Yuri on 2015/7/22.
 */
public class UnitTestActivity extends ActivityInstrumentationTestCase2<MainActivity> {
    public UnitTestActivity(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //初始化
    }


}

package com.yuri.dreamlinkcost.view.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuri.dreamlinkcost.R;

import tyrantgit.explosionfield.ExplosionField;

public class SplashActivity2 extends AppCompatActivity {

    private ExplosionField mExplosionField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mExplosionField = ExplosionField.attach2Window(this);

        ImageView imageView = (ImageView) findViewById(R.id.iv_logo);
        TextView textView = (TextView) findViewById(R.id.tv_theme_title);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExplosionField.explode(v);
            }
        });


//        explosionField.explode(textView);
    }

}

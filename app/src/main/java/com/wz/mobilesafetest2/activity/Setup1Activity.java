package com.wz.mobilesafetest2.activity;

import android.os.Bundle;

import com.wz.mobilesafetest2.R;

public class Setup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void nextActivity() {
        startActivity(Setup2Activity.class);
    }

    @Override
    public void prevActivity() {

    }

}

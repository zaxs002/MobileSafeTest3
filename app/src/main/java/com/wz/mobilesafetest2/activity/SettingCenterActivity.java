package com.wz.mobilesafetest2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wz.mobilesafetest2.R;
import com.wz.mobilesafetest2.service.BlackService;
import com.wz.mobilesafetest2.utils.SafeConstants;
import com.wz.mobilesafetest2.utils.ServiceUtils;
import com.wz.mobilesafetest2.utils.SpTools;
import com.wz.mobilesafetest2.view.SettingView;

public class SettingCenterActivity extends Activity {

    private SettingView mStAutoUpdate;
    private SettingView mStBlack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initData();

        initEvent();
    }

    private void initData() {
        boolean autoUpdate = SpTools.getBoolean(getApplicationContext(), SafeConstants.AUTOUPDATE, false);
        boolean isOpenBlack = SpTools.getBoolean(getApplicationContext(), SafeConstants.BLACK, false);

        boolean isBlackServiceRunning = ServiceUtils.isRunningService(getApplicationContext(), BlackService.class.getName());
        mStBlack.setToggleOn(isBlackServiceRunning);
        mStAutoUpdate.setToggleOn(autoUpdate);
    }

    private void initEvent() {
        mStAutoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mStBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingCenterActivity.this, BlackService.class);
                if (mStBlack.isToggleOn()) {
                    //关闭
                    SpTools.putBoolean(getApplicationContext(),SafeConstants.BLACK,false);
                    stopService(intent);
                } else {
                    //开启
                    SpTools.putBoolean(getApplicationContext(),SafeConstants.BLACK,true);
                    startService(intent);
                }
                mStBlack.toggle();
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_setting_center);

        mStAutoUpdate = (SettingView) findViewById(R.id.st_settingcenter_autoupdate);
        mStBlack = (SettingView) findViewById(R.id.st_settingcenter_black);
    }
}

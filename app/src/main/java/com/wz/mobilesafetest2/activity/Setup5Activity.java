package com.wz.mobilesafetest2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.wz.mobilesafetest2.R;
import com.wz.mobilesafetest2.service.LostFindService;
import com.wz.mobilesafetest2.utils.SafeConstants;
import com.wz.mobilesafetest2.utils.ServiceUtils;
import com.wz.mobilesafetest2.utils.SpTools;

public class Setup5Activity extends BaseSetupActivity {

    private CheckBox mCbIsSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup5);

        mCbIsSetup = (CheckBox) findViewById(R.id.cb_setup5_isactivation);
    }

    @Override
    public void initData() {
        String name = LostFindService.class.getName();
        if (ServiceUtils.isRunningService(this,name)) {
            mCbIsSetup.setChecked(true);
        } else {
            mCbIsSetup.setChecked(false);
        }
    }

    @Override
    public void initEvent() {
        super.initEvent();

        mCbIsSetup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(Setup5Activity.this, LostFindService.class);
                if (isChecked) {
                    SpTools.putBoolean(getApplicationContext(), SafeConstants.ISOPEN, true);
                    startService(intent);
                    Toast.makeText(Setup5Activity.this, "服务已经开启", Toast.LENGTH_SHORT).show();
                } else {
                    SpTools.putBoolean(getApplicationContext(), SafeConstants.ISOPEN, false);
                    stopService(intent);
                    Toast.makeText(Setup5Activity.this, "服务已经关闭", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void nextActivity() {
        if (ServiceUtils.isRunningService(this,LostFindService.class.getName())) {
            SpTools.putBoolean(getApplicationContext(), SafeConstants.ISSETUP, true);
            startActivity(LostFindActivity.class);
        } else {
            Toast.makeText(Setup5Activity.this, "请先开启防盗保护", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void prevActivity() {
        startActivity(Setup4Activity.class);
    }
}

package com.wz.mobilesafetest2.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gc.materialdesign.views.Button;
import com.wz.mobilesafetest2.R;
import com.wz.mobilesafetest2.receiver.DevicePolicyReceiver;
import com.wz.mobilesafetest2.utils.SafeConstants;
import com.wz.mobilesafetest2.utils.SpTools;

public class Setup4Activity extends BaseSetupActivity {

    private static final int GOACTIVATION = 1;
    private static final int GOCANCEL = 2;
    private ImageView mIvIsActive;
    private Button mBtActivation;
    private ComponentName who;
    private DevicePolicyManager dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void initView() {
        setContentView(R.layout.activity_setup4);
        dm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        who = new ComponentName(Setup4Activity.this, DevicePolicyReceiver.class);

        mBtActivation = (Button) findViewById(R.id.bt_setup4_activation);
        mIvIsActive = (ImageView) findViewById(R.id.iv_setup4_isactive);

        if (dm.isAdminActive(who)) {
            mIvIsActive.setImageResource(R.drawable.admin_activated);
        } else {
            mIvIsActive.setImageResource(R.drawable.admin_inactivated);
        }
    }


    @Override
    public void initData() {
        super.initData();

    }

    /*
    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启手机防盗相关功能");
    startActivityForResult(intent, 1);
    */
    @Override
    public void initEvent() {
        super.initEvent();
        mBtActivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dm.isAdminActive(who)) {
                    dm.removeActiveAdmin(who);
                    Toast.makeText(Setup4Activity.this, "取消成功!", Toast.LENGTH_SHORT).show();
                    mIvIsActive.setImageResource(R.drawable.admin_inactivated);
                } else {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启手机防盗相关功能");
                    startActivityForResult(intent, GOACTIVATION);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case GOACTIVATION:
                if (dm.isAdminActive(who)) {
                    Toast.makeText(Setup4Activity.this, "激活了", Toast.LENGTH_SHORT).show();
                    mIvIsActive.setImageResource(R.drawable.admin_activated);
                    SpTools.putBoolean(getApplicationContext(), SafeConstants.ISACTIVATION, true);
                } else {
                    Toast.makeText(Setup4Activity.this, "不激活可不行喔!", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void nextActivity() {
        if (dm.isAdminActive(who)) {
            startActivity(Setup5Activity.class);
        } else {
            Toast.makeText(Setup4Activity.this, "请激活!!!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void prevActivity() {
        startActivity(Setup3Activity.class);
    }
}

package com.wz.mobilesafetest2.activity;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.gc.materialdesign.views.Button;
import com.wz.mobilesafetest2.R;
import com.wz.mobilesafetest2.utils.SafeConstants;
import com.wz.mobilesafetest2.utils.SpTools;

public class Setup2Activity extends BaseSetupActivity {

    private Button mBtBind;
    private String sim;
    private ImageView mIvBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup2);

        sim = SpTools.getString(getApplicationContext(), SafeConstants.ISBIND, "");
        mBtBind = (Button) findViewById(R.id.bt_setup2_bind);
        mIvBind = (ImageView) findViewById(R.id.iv_setup2_bind);

        if (!TextUtils.isEmpty(sim)) {
            mIvBind.setImageResource(R.drawable.lock);
        }else{
            mIvBind.setImageResource(R.drawable.unlock);
        }
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initEvent() {
        mBtBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String simSer = SpTools.getString(getApplicationContext(), SafeConstants.ISBIND, "");
                if (!TextUtils.isEmpty(simSer)) {
                    SpTools.putString(getApplicationContext(), SafeConstants.ISBIND, "");
                    mIvBind.setImageResource(R.drawable.unlock);
                }else{
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();
                    SpTools.putString(getApplicationContext(),SafeConstants.ISBIND,simSerialNumber);
                    mIvBind.setImageResource(R.drawable.lock);
                }
            }
        });
    }

    @Override
    public void nextActivity() {
        String simSer = SpTools.getString(getApplicationContext(), SafeConstants.ISBIND, "");
        if (!TextUtils.isEmpty(simSer)) {
            startActivity(Setup3Activity.class);
        }else{
            Toast.makeText(Setup2Activity.this, "请先绑定SIM卡", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public void prevActivity() {
        startActivity(Setup1Activity.class);
    }
}

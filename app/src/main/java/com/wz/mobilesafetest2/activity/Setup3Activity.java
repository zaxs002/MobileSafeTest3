package com.wz.mobilesafetest2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gc.materialdesign.views.Button;
import com.wz.mobilesafetest2.R;
import com.wz.mobilesafetest2.utils.SafeConstants;
import com.wz.mobilesafetest2.utils.SpTools;

public class Setup3Activity extends BaseSetupActivity {

    private EditText mEtSafenumber;
    private Button mBtSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup3);
        mEtSafenumber = (EditText) findViewById(R.id.et_setup3_safenumber);
        mBtSelect = (Button) findViewById(R.id.bt_setup3_select);

    }

    @Override
    public void initData() {
        super.initData();
        String safenumber = SpTools.getString(getApplicationContext(), SafeConstants.SAFENUMBER, "");
        mEtSafenumber.setText(safenumber);
    }

    @Override
    public void initEvent() {
        super.initEvent();
        mBtSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Setup3Activity.this, ChooseContactActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String phone = data.getStringExtra("phone");
            mEtSafenumber.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void nextActivity() {
        String safeNumber = mEtSafenumber.getText().toString().trim();
        if (TextUtils.isEmpty(safeNumber)) {
            Toast.makeText(Setup3Activity.this, "安全号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else {
            SpTools.putString(getApplicationContext(), SafeConstants.SAFENUMBER,safeNumber);
            startActivity(Setup4Activity.class);
        }
    }

    @Override
    public void prevActivity() {
        startActivity(Setup2Activity.class);
    }
}

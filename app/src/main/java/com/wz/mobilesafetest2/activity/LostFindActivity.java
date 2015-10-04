package com.wz.mobilesafetest2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wz.mobilesafetest2.R;
import com.wz.mobilesafetest2.service.LostFindService;
import com.wz.mobilesafetest2.utils.SafeConstants;
import com.wz.mobilesafetest2.utils.ServiceUtils;
import com.wz.mobilesafetest2.utils.SpTools;

public class LostFindActivity extends AppCompatActivity {

    private TextView tv_reset;
    private ImageView mIvLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SpTools.getBoolean(getApplicationContext(), SafeConstants.ISSETUP, false)) {
            InitView();
            initEvent();
        } else {
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            finish();
        }


    }

    private void initEvent() {
        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpTools.putBoolean(getApplicationContext(), SafeConstants.ISSETUP, false);
                Intent intent = new Intent(LostFindActivity.this, Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void InitView() {
        setContentView(R.layout.activity_lost_find);
        tv_reset = (TextView) findViewById(R.id.tv_lostfind_reset);
        mIvLock = (ImageView) findViewById(R.id.iv_lostfind_lock);
        TextView mTvPhone = (TextView) findViewById(R.id.tv_lostfind_phone);

        String safeNumber = SpTools.getString(getApplicationContext(), SafeConstants.SAFENUMBER, "");
        boolean runningService = ServiceUtils.isRunningService(this, LostFindService.class.getName());
        mTvPhone.setText(safeNumber);
        if (runningService) {
            mIvLock.setImageResource(R.drawable.lock);
        } else {
            mIvLock.setImageResource(R.drawable.unlock);
        }
    }
}

package com.wz.mobilesafetest2.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wz.mobilesafetest2.R;
import com.wz.mobilesafetest2.bean.HomeItem;
import com.wz.mobilesafetest2.utils.Md5Utils;
import com.wz.mobilesafetest2.utils.SafeConstants;
import com.wz.mobilesafetest2.utils.SpTools;
import com.wz.mobilesafetest2.view.WaterMelonButton;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private ImageView mIvLogo;

    private final static int[] icons = new int[]{
            R.drawable.sjfd, R.drawable.srlj,
            R.drawable.rjgj, R.drawable.jcgl,
            R.drawable.lltj, R.drawable.sjsd,
            R.drawable.hcql, R.drawable.cygj
    };

    private final static String[] TITLES = new String[]{"手机防盗", "骚扰拦截",
            "软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具"};
    private final static String[] DESCS = new String[]{"远程定位手机", "全面拦截骚扰",
            "管理您的软件", "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全"};
    private GridView mGvItems;
    private List<HomeItem> mDatas = new ArrayList();
    private HomeAdapter adapter;
    private AlertDialog dialog;
    private AlertDialog dialog1;
    private ImageView mIvSetting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initAnimation();

        initData();

        initEvent();
    }

    private void initEvent() {
        mGvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        String password = SpTools.getString(getApplicationContext(), SafeConstants.PASSWORD, "");
                        if (TextUtils.isEmpty(password)) {
                            showSetpasswordDialig();
                        } else {
                            showEnterPasswordDialog(password);
                        }
                        break;
                    case 1:
                        Intent intent = new Intent(HomeActivity.this, AndroidBlackActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
        mIvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingCenterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showEnterPasswordDialog(final String password) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        View view = View.inflate(HomeActivity.this,R.layout.dialog_enterpassword,null);
        builder.setView(view);
        final EditText et_enter_password = (EditText) view.findViewById(R.id.et_dialog_enterpassword);
        WaterMelonButton  bt_enter_confirm = (WaterMelonButton ) view.findViewById(R.id.bt_dialog_enterpassword_confirm);
        WaterMelonButton  bt_enter_cancel = (WaterMelonButton ) view.findViewById(R.id.bt_dialog_enterpassword_cancel);
        bt_enter_cancel.setRippleSpeed(60);
        bt_enter_confirm.setRippleSpeed(60);

        bt_enter_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });
        
        bt_enter_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwdSrc = et_enter_password.getText().toString().trim();
                if (TextUtils.isEmpty(pwdSrc)) {
                    Toast.makeText(HomeActivity.this, "不能为空喔", Toast.LENGTH_SHORT).show();
                    return;
                }
                String pwd = Md5Utils.md5(Md5Utils.md5(pwdSrc));
                if (pwd.equals(password)) {
                    //进入手机防盗界面
                    Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
                    startActivity(intent);
                    dialog1.dismiss();
                }else{
                    Toast.makeText(HomeActivity.this, "密码不正确,请重试!", Toast.LENGTH_SHORT).show();
                    et_enter_password.setText("");
                }
            }
        });
        dialog1 = builder.show();
    }

    private void showSetpasswordDialig() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        View view1 = View.inflate(HomeActivity.this, R.layout.dialog_setpassword, null);
        builder.setView(view1);
        final EditText et_pwone = (EditText) view1.findViewById(R.id.et_dialog_setpassword_one);
        final EditText et_pwtwo = (EditText) view1.findViewById(R.id.et_dialog_setpassword_two);
        WaterMelonButton  bt_confirm = (WaterMelonButton ) view1.findViewById(R.id.bt_dialog_setpassword_comfirm);
        WaterMelonButton  bt_cancel = (WaterMelonButton ) view1.findViewById(R.id.bt_dialog_setpassword_cancel);
        bt_confirm.setRippleSpeed(60);
        bt_cancel.setRippleSpeed(60);

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwone = et_pwone.getText().toString().trim();
                String pwtwo = et_pwtwo.getText().toString().trim();
                if (TextUtils.isEmpty(pwone) || TextUtils.isEmpty(pwtwo)) {
                    Toast.makeText(HomeActivity.this, "不能为空,谢谢", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pwone.equals(pwtwo)) {
                    Toast.makeText(HomeActivity.this, "密码必须相等", Toast.LENGTH_SHORT).show();
                    return;
                }
                SpTools.putString(getApplicationContext(),SafeConstants.PASSWORD, Md5Utils.md5(Md5Utils.md5(pwone)));
                Toast.makeText(HomeActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog = builder.show();
    }

    private void initData() {
        for (int i = 0; i < icons.length; i++) {
            HomeItem item = new HomeItem();
            item.setIconId(icons[i]);
            item.setTitle(TITLES[i]);
            item.setDesc(DESCS[i]);
            mDatas.add(item);
        }
        adapter = new HomeAdapter();
        mGvItems.setAdapter(adapter);
    }

    private void initView() {
        setContentView(R.layout.activity_home);

        mIvLogo = (ImageView) findViewById(R.id.iv_home_logo);
        mGvItems = (GridView) findViewById(R.id.gv_home_items);
        mIvSetting = (ImageView) findViewById(R.id.iv_home_setting);
    }

    private void initAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIvLogo, "rotationY", 0, 90, 270, 360);
        animator.setDuration(5000);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.start();
    }

    public void setting(View view) {

    }

    private class HomeAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            if (mDatas != null) {
                return mDatas.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = View.inflate(HomeActivity.this, R.layout.item_home_gv, null);
            } else {
                view = convertView;
            }
            TextView tv_home_item_title = (TextView) view.findViewById(R.id.tv_home_item_title);
            ImageView iv_home_item_icon = (ImageView) view.findViewById(R.id.iv_home_item_icon);
            TextView tv_home_item_desc = (TextView) view.findViewById(R.id.tv_home_item_desc);

            HomeItem item = mDatas.get(position);
            tv_home_item_title.setText(item.getTitle());
            tv_home_item_desc.setText(item.getDesc());
            iv_home_item_icon.setImageResource(item.getIconId());

            return view;
        }
    }
}

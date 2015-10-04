package com.wz.mobilesafetest2.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wz.mobilesafetest2.R;
import com.wz.mobilesafetest2.bean.ContactBean;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSmsContactActivity extends Activity {


    private static final int UPDATELIST = 0x66;
    private List<ContactBean> mContacts = new ArrayList();
    private int[] avatars = new int[]{
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4
    };
    private ChooseAdapter adapter;
    private ProgressDialog loadingDialog;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATELIST:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    private ListView mLvChoose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initData();

        initEvent();
    }

    private void initEvent() {
        mLvChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactBean bean = mContacts.get(position);
                Intent intent = new Intent();
                intent.putExtra("phone", bean.getPhone());
                setResult(1, intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        adapter = null;
        mLvChoose = null;
        super.onDestroy();
    }

    private void initView() {
        setContentView(R.layout.activity_choose_contact);
        mLvChoose = (ListView) findViewById(R.id.lv_chooseContact);

        adapter = new ChooseAdapter();
        mLvChoose.setAdapter(adapter);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("正在疯狂载入中...");
        loadingDialog.show();
    }
    private void initData() {
        new Thread(){
            @Override
            public void run() {
                mContacts = getData();
                mHandler.sendEmptyMessage(UPDATELIST);
                loadingDialog.dismiss();
            }
        }.start();
    }

    public abstract List<ContactBean> getData();


    private class ChooseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mContacts.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(),R.layout.item_choosecontact,null);
            }else{
                view = convertView;
            }
            ImageView iv_avator = (ImageView) view.findViewById(R.id.iv_choose_avatar);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_choose_name);
            TextView tv_phone = (TextView) view.findViewById(R.id.tv_choose_phone);
            iv_avator.setImageResource(avatars[position % 4]);
            tv_name.setText(mContacts.get(position).getName());
            tv_phone.setText(mContacts.get(position).getPhone());

            return view;
        }
    }
}

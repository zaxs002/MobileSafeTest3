package com.wz.mobilesafetest2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wz.mobilesafetest2.R;
import com.wz.mobilesafetest2.bean.BlackBean;
import com.wz.mobilesafetest2.dao.BlackDao;
import com.wz.mobilesafetest2.db.BlackDb;

import java.util.ArrayList;
import java.util.List;

public class WebBlackActivity extends Activity {


    private static final int LOADING = 0x66;
    private static final int FINISH = 0x88;
    private static final int REQUEST_SMS = 1;
    private static final int REQUEST_CALLLOG = 2;
    private static final int REQUEST_CONTACT = 3;
    private static final int COUNTSPERPAGE = 10;
    private int totalPages = 0;
    private int currentPage = 1;
    private ListView mLvData;
    private ImageView mIvEmpty;
    private Button mBtAdd;
    private List<BlackBean> mDatas = new ArrayList();
    private BlackAdapter adapter;
    private BlackDao dao;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    mPbLoading.setVisibility(View.VISIBLE);
                    mIvEmpty.setVisibility(View.GONE);
                    mLvData.setVisibility(View.GONE);
                    break;
                case FINISH:
                    mPbLoading.setVisibility(View.GONE);
                    if (mDatas.isEmpty()) {
                        mLvData.setVisibility(View.GONE);
                        mIvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        mLvData.setVisibility(View.VISIBLE);
                        mIvEmpty.setVisibility(View.GONE);

                        adapter.notifyDataSetChanged();
                        if (mIsFirst) {
                            mLvData.smoothScrollToPosition(0);
                            mIsFirst = false;
                        }
                        tv_current.setText(currentPage + "/" + totalPages);
                        et_jump.setText(currentPage+"");
                        et_jump.setSelection(et_jump.getText().toString().trim().length());
                    }
                    break;
            }
        }
    };
    private ProgressBar mPbLoading;
    private PopupWindow mPopMenu;
    private View mViewPop;
    private ScaleAnimation animation;
    private AlertDialog alertDialog;
    private boolean mIsFirst;
    private EditText et_number;
    private Button bt_first;
    private Button bt_last;
    private Button bt_next;
    private Button bt_prev;
    private TextView tv_current;
    private EditText et_jump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initData();

        initEvent();
    }

    private void initView() {
        setContentView(R.layout.activity_web_black);

        mLvData = (ListView) findViewById(R.id.lv_black_data);
        mIvEmpty = (ImageView) findViewById(R.id.iv_black_empty);
        mBtAdd = (Button) findViewById(R.id.bt_black_add);

        bt_first = (Button) findViewById(R.id.bt_web_first);
        bt_last = (Button) findViewById(R.id.bt_web_last);
        bt_next = (Button) findViewById(R.id.bt_web_next);
        bt_prev = (Button) findViewById(R.id.bt_web_prev);
        tv_current = (TextView) findViewById(R.id.tv_web_current);
        et_jump = (EditText) findViewById(R.id.et_web_jump);

        mPbLoading = (ProgressBar) findViewById(R.id.pb_black_loading);

        adapter = new BlackAdapter();
        mLvData.setAdapter(adapter);
        initPopWindow();
        initDialog();
    }

    private void initData() {
        animation = new ScaleAnimation(1f, 1f, 0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f);
        animation.setDuration(200);
        dao = new BlackDao(this);
        new Thread() {
            @Override
            public void run() {
                //mHandler.sendEmptyMessage(LOADING);
                mHandler.obtainMessage(LOADING).sendToTarget();
                int allRow = dao.getAllRow();
                totalPages = (int) Math.ceil(allRow * 1.0 / COUNTSPERPAGE);
                mDatas = dao.getPageData(currentPage,COUNTSPERPAGE);
                //mHandler.sendEmptyMessage(FINISH);
                mHandler.obtainMessage(FINISH).sendToTarget();
            }
        }.start();
    }

    private void initEvent() {
        mBtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopMenu != null && mPopMenu.isShowing()) {
                    mPopMenu.dismiss();
                } else {
                    mPopMenu.showAsDropDown(v);
                    mViewPop.startAnimation(animation);
                }
            }
        });

        PageListener pageListener = new PageListener();
        bt_first.setOnClickListener(pageListener);
        bt_last.setOnClickListener(pageListener);
        bt_next.setOnClickListener(pageListener);
        bt_prev.setOnClickListener(pageListener);

    }

    private void initPopWindow() {
        mViewPop = View.inflate(getApplicationContext(), R.layout.layout_popwindow, null);

        TextView bt_pop_contact = (TextView) mViewPop.findViewById(R.id.tv_pop_contact);
        TextView bt_pop_phone = (TextView) mViewPop.findViewById(R.id.tv_pop_phone);
        TextView bt_pop_showdong = (TextView) mViewPop.findViewById(R.id.tv_pop_shoudong);
        TextView bt_pop_sms = (TextView) mViewPop.findViewById(R.id.tv_pop_sms);

        View.OnClickListener btClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_pop_contact:
                        Toast.makeText(WebBlackActivity.this, "contact", Toast.LENGTH_SHORT).show();
                        contactAdd();
                        break;
                    case R.id.tv_pop_phone:
                        Toast.makeText(WebBlackActivity.this, "phone", Toast.LENGTH_SHORT).show();
                        phoneAdd();
                        break;
                    case R.id.tv_pop_sms:
                        Toast.makeText(WebBlackActivity.this, "sms", Toast.LENGTH_SHORT).show();
                        smsAdd();
                        break;
                    case R.id.tv_pop_shoudong:
                        Toast.makeText(WebBlackActivity.this, "shoudong", Toast.LENGTH_SHORT).show();
                        shoudongAdd();
                        break;
                }
                mPopMenu.dismiss();
            }
        };
        bt_pop_contact.setOnClickListener(btClickListener);
        bt_pop_phone.setOnClickListener(btClickListener);
        bt_pop_showdong.setOnClickListener(btClickListener);
        bt_pop_sms.setOnClickListener(btClickListener);

        mPopMenu = new PopupWindow(mViewPop, -2, -2);

        mPopMenu.setFocusable(true);
        mPopMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopMenu.setOutsideTouchable(true);
        mPopMenu.setContentView(mViewPop);
    }

    private void phoneAdd() {
        Intent intent = new Intent(this, CallLogActivity.class);
        startActivityForResult(intent, REQUEST_CALLLOG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String phone = data.getStringExtra("phone");
            showDialog(phone);
        } else {
            Toast.makeText(WebBlackActivity.this, "123", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void smsAdd() {
        Intent intent = new Intent(this, SmsActivity.class);
        startActivityForResult(intent, REQUEST_SMS);
    }

    private void shoudongAdd() {
        showDialog("");
    }

    private void showDialog(String phone) {
        et_number.setText(phone);
        alertDialog.show();
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(getApplicationContext(), R.layout.dialog_addblack, null);
        builder.setView(view);
        et_number = (EditText) view.findViewById(R.id.et_dilog_addblack_number);
        final CheckBox cb_phone = (CheckBox) view.findViewById(R.id.cb_dialog_addblack_mode_phone);
        final CheckBox cb_sms = (CheckBox) view.findViewById(R.id.cb_dialog_addblack_mode_sms);
        Button bt_confirm = (Button) view.findViewById(R.id.bt_dialog_addblack_confirm);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_dialog_addblack_cancel);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = et_number.getText().toString().trim();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(WebBlackActivity.this, "不能为空喔!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!cb_phone.isChecked() && !cb_sms.isChecked()) {
                    Toast.makeText(WebBlackActivity.this, "至少要选择一种拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                BlackBean bean = new BlackBean();
                int mode = 0;
                if (cb_sms.isChecked()) {
                    mode |= BlackDb.MODE_SMS;
                }
                if (cb_phone.isChecked()) {
                    mode |= BlackDb.MODE_PHONE;
                }

                bean.setMode(mode);
                bean.setPhone(number);

                dao.update(bean);

                mIsFirst = true;
                currentPage = 1;
                initData();

                alertDialog.dismiss();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog = builder.create();
    }

    private void contactAdd() {
        Intent intent = new Intent(this, ChooseContactActivity.class);
        startActivityForResult(intent, REQUEST_CONTACT);
    }

    public void jump(View view) {
        String pageStr = et_jump.getText().toString().trim();
        int page = Integer.parseInt(pageStr);
        if (page < 1 || page > totalPages) {
            Toast.makeText(WebBlackActivity.this, "别出老千", Toast.LENGTH_SHORT).show();
            return;
        }
        currentPage = page;
        initData();
    }

    private class BlackAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_black, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_black_item_delete);
                viewHolder.tv_mode = (TextView) convertView.findViewById(R.id.tv_black_item_mode);
                viewHolder.tv_phone = (TextView) convertView.findViewById(R.id.tv_black_item_phone);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final BlackBean bean = (BlackBean) getItem(position);

            viewHolder.tv_phone.setText(bean.getPhone());
            switch (bean.getMode()) {
                case BlackDb.MODE_SMS:
                    viewHolder.tv_mode.setText("短信拦截");
                    break;
                case BlackDb.MODE_PHONE:
                    viewHolder.tv_mode.setText("电话拦截");
                    break;
                case BlackDb.MODE_ALL:
                    viewHolder.tv_mode.setText("全部拦截");
                    break;
            }
            viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WebBlackActivity.this);
                    builder.setTitle("注意");
                    builder.setMessage("是否删除数据?");
                    builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dao.delete(bean.getPhone());
                            initData();
                        }
                    });
                    builder.setNegativeButton("不删", null);
                    builder.show();
                }
            });

            return convertView;
        }
    }

    private class ViewHolder {
        TextView tv_phone;
        TextView tv_mode;
        ImageView iv_delete;
    }

    private class PageListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.bt_web_first:
                    currentPage = 1;
                    initData();
                    break;
                case R.id.bt_web_last:
                    currentPage = totalPages;
                    initData();
                    break;
                case R.id.bt_web_next:
                    if (currentPage == totalPages) {
                        Toast.makeText(WebBlackActivity.this, "已经是最后一页", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    currentPage++;
                    initData();
                    break;
                case R.id.bt_web_prev:
                    if (currentPage == 1) {
                        Toast.makeText(getApplicationContext(), "已经是第一页", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    currentPage--;
                    initData();
                    break;
            }
        }
    }

}

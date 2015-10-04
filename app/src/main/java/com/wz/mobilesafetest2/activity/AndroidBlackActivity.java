package com.wz.mobilesafetest2.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wz.mobilesafetest2.R;
import com.wz.mobilesafetest2.bean.BlackBean;
import com.wz.mobilesafetest2.dao.BlackDao;
import com.wz.mobilesafetest2.db.BlackDb;
import com.wz.mobilesafetest2.view.RefreshListView;

import java.util.List;
import java.util.Vector;

public class AndroidBlackActivity extends AppCompatActivity {

    private static final int LOADING = 0x66;
    private static final int FINISH = 0x88;
    private static final int REQUEST_SMS = 1;
    private static final int REQUEST_CALLLOG = 2;
    private static final int REQUEST_CONTACT = 3;
    private static final int COUNTSPERLOAD = 15;
    private RefreshListView mLvData;
    private ImageView mIvEmpty;
    private Button mBtAdd;
    private List<BlackBean> mDatas = new Vector();
    private BlackAdapter adapter;
    private BlackDao dao;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    //mPbLoading.setVisibility(View.VISIBLE);
                    mIvEmpty.setVisibility(View.GONE);
                    mLvData.setVisibility(View.VISIBLE);
                    break;
                case FINISH:
                    mLvData.finishRefreshing();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initData();

        initEvent();
    }

    private void initView() {
        setContentView(R.layout.activity_android_black);

        mLvData = (RefreshListView) findViewById(R.id.lv_black_data);
        mLvData.isEnableRefreshFoot(true);
        mLvData.isEnableRefreshHead(true);
        mIvEmpty = (ImageView) findViewById(R.id.iv_black_empty);
        mBtAdd = (Button) findViewById(R.id.bt_black_add);

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
                SystemClock.sleep(1000);
                List<BlackBean> loadMore = dao.loadMore(mDatas.size(), COUNTSPERLOAD);
                mDatas.addAll(loadMore);
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
        mLvData.setOnRefreshingDataListener(new RefreshListView.OnRefreshingDataListener() {
            @Override
            public void onHeadRefreshing() {
                mDatas.clear();
                initData();
            }

            @Override
            public void onFooterFreshing() {
                initData();
            }
        });

//        mLvData.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//                    int lastVisiblePosition = mLvData.getLastVisiblePosition();
//                    System.out.println("lastVisiblePosition: " + lastVisiblePosition);
//                    System.out.println(mDatas.size() - 1);
//                    if (lastVisiblePosition >= (mDatas.size() - 1)) {
//                        initData();
//                    }
//                }
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//            }
//        });
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
                        Toast.makeText(AndroidBlackActivity.this, "contact", Toast.LENGTH_SHORT).show();
                        contactAdd();
                        break;
                    case R.id.tv_pop_phone:
                        Toast.makeText(AndroidBlackActivity.this, "phone", Toast.LENGTH_SHORT).show();
                        phoneAdd();
                        break;
                    case R.id.tv_pop_sms:
                        Toast.makeText(AndroidBlackActivity.this, "sms", Toast.LENGTH_SHORT).show();
                        smsAdd();
                        break;
                    case R.id.tv_pop_shoudong:
                        Toast.makeText(AndroidBlackActivity.this, "shoudong", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(AndroidBlackActivity.this, "123", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AndroidBlackActivity.this, "不能为空喔!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!cb_phone.isChecked() && !cb_sms.isChecked()) {
                    Toast.makeText(AndroidBlackActivity.this, "至少要选择一种拦截模式", Toast.LENGTH_SHORT).show();
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
                mDatas.clear();
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
                    mDatas.remove(bean);
                    dao.delete(bean.getPhone());
                    adapter.notifyDataSetChanged();
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
}

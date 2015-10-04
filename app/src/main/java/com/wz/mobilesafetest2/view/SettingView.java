package com.wz.mobilesafetest2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wz.mobilesafetest2.R;

public class SettingView extends RelativeLayout {

    private final static int BKG_FIRST = 0;
    private final static int BKG_MIDDLE = 1;
    private final static int BKG_LAST = 2;

    private TextView mTvTitle;
    private ImageView mIvToggle;

    private boolean isToggleOn;

    public SettingView(Context context) {
        this(context, null);
    }

    public SettingView(Context context, AttributeSet set) {
        super(context, set);

        // 将布局和view绑定
        View view = View.inflate(context, R.layout.activity_setting_view, this);

        mTvTitle = (TextView) findViewById(R.id.tv_view_title);
        mIvToggle = (ImageView) findViewById(R.id.iv_view_toggle);

        // 读取自定的属性
        TypedArray ta = context.obtainStyledAttributes(set,
                R.styleable.SettingItem);

        String title = ta.getString(R.styleable.SettingItem_setting_title);
        int bkg = ta.getInt(R.styleable.SettingItem_itebackground, 0);

        // 回收
        ta.recycle();

        mTvTitle.setText(title);

        // 设置背景
        switch (bkg) {
            case BKG_FIRST:
                view.setBackgroundResource(R.drawable.setting_center_first_selector);
                break;
            case BKG_MIDDLE:
                view.setBackgroundResource(R.drawable.setting_center_middle_selector);
                break;
            case BKG_LAST:
                view.setBackgroundResource(R.drawable.setting_center_last_selector);
                break;
            default:
                view.setBackgroundResource(R.drawable.setting_center_first_selector);
                break;
        }

        // 设置默认值
        setToggleOn(isToggleOn);
    }

    // 设置开关打开还是关闭
    public void setToggleOn(boolean on) {
        this.isToggleOn = on;
        if (on) {
            mIvToggle.setImageResource(R.drawable.on);
        } else {
            mIvToggle.setImageResource(R.drawable.off);
        }
    }

    // 如果打开就关闭，如果关闭就打开
    public void toggle() {
        setToggleOn(!isToggleOn);
    }

    public boolean isToggleOn() {
        return isToggleOn;
    }


}

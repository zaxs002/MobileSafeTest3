package com.wz.mobilesafetest2.view;

import android.content.Context;
import android.util.AttributeSet;

import com.gc.materialdesign.views.ButtonRectangle;

/**
 * Created by Administrator on 2015/10/1 0001.
 */
public class WaterMelonButton extends ButtonRectangle {

    public WaterMelonButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRippleSpeed(60);
    }
}

package com.wz.mobilesafetest2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.wz.mobilesafetest2.R;

public abstract class BaseSetupActivity extends AppCompatActivity {

    private GestureDetector mGd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        initData();

        initEvent();

        initGuesture();
    }

    private void initGuesture() {
        mGd = new GestureDetector(new MyOnGestureListner() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e2.getY() - e1.getY())) {
                    if (Math.abs(velocityX) > 50) {
                        if (velocityX > 0) {
                            prev(null);
                        } else {
                            next(null);
                        }
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGd != null) {
            mGd.onTouchEvent(event);
            return true;
        }
        return super.onTouchEvent(event);
    }

    public void initEvent() {

    }

    public void initData() {

    }

    public void initView() {
        setContentView(R.layout.activity_base_setup);
    }

    public void next(View v){
        nextActivity();

        nextAnimation();
    }

    public void prev(View v){
        prevActivity();

        prevAnimation();
    }

    private void nextAnimation() {
        overridePendingTransition(R.anim.next_enter_anim,R.anim.next_exit_anim);
    }
    private void prevAnimation() {
        overridePendingTransition(R.anim.prev_enter_anim,R.anim.prev_exit_anim);
    }

    public abstract void nextActivity();
    public abstract void prevActivity();

    public void startActivity(Class type){
        Intent intent = new Intent(this, type);
        startActivity(intent);
        finish();
    }

    private class MyOnGestureListner implements GestureDetector.OnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}

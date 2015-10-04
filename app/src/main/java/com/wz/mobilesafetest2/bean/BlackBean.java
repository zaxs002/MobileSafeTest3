package com.wz.mobilesafetest2.bean;

/**
 * Created by Administrator on 2015/10/2 0002.
 */
public class BlackBean {
    private String phone;
    private int mode;

    public BlackBean(String phone, int mode) {
        this.phone = phone;
        this.mode = mode;
    }

    public BlackBean() {

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}

package com.wz.mobilesafetest2.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.IBinder;

public class LockScreenService extends Service {
    public LockScreenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DevicePolicyManager dm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        dm.resetPassword("123",0);
        dm.lockNow();
        stopSelf();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}

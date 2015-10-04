package com.wz.mobilesafetest2.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.wz.mobilesafetest2.dao.BlackDao;
import com.wz.mobilesafetest2.dao.ContactDao;
import com.wz.mobilesafetest2.db.BlackDb;

import java.lang.reflect.Method;

public class BlackService extends Service {

    private SmsReceiver smsReceiver;
    private BlackDao dao;
    private PhoneStateListener listener;
    private TelephonyManager tm;

    public BlackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackDao(getApplicationContext());
        registerSmsInterupt();

        regiterTelInterupt();
    }

    private void regiterTelInterupt() {
        tm = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        System.out.println("电话拦截..");
                        phoneInterupt(incomingNumber);
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void phoneInterupt(String incomingNumber) {
        int mode = dao.getMode(incomingNumber);
        if ((mode & BlackDb.MODE_PHONE) != 0) {
            deletelog(incomingNumber);
            endCall();
        }
    }

    private void endCall() {
        try {
            Class<?> aClass = Class.forName("android.os.ServiceManager");
            Method method = aClass.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (android.os.IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
            iTelephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deletelog(final String incomingNumber) {
        final Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()){
            @Override
            public void onChange(boolean selfChange) {
                ContactDao.deletelog(getApplicationContext(),uri,incomingNumber);
                getContentResolver().unregisterContentObserver(this);
                super.onChange(selfChange);
            }
        });
    }

    private void registerSmsInterupt() {
        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(smsReceiver, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(smsReceiver);
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    private class SmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object pdu : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                String number = sms.getOriginatingAddress();
                int mode = dao.getMode(number);
                System.out.println(mode);
                System.out.println(mode & BlackDb.MODE_SMS);
                if ((mode & BlackDb.MODE_SMS) != 0){
                    System.out.println("被拦截了");
                    abortBroadcast();
                }
            }
        }
    }
}

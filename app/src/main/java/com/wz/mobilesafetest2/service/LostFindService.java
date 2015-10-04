package com.wz.mobilesafetest2.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.telephony.SmsMessage;

import com.wz.mobilesafetest2.R;
import com.wz.mobilesafetest2.utils.SafeConstants;
import com.wz.mobilesafetest2.utils.SpTools;

public class LostFindService extends Service {

    private SmsReceiver smsReceiver;

    public LostFindService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(smsReceiver, filter);
        String safeNumber = SpTools.getString(getApplicationContext(), SafeConstants.SAFENUMBER, "");

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(smsReceiver);
        super.onDestroy();
    }

    class SmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");

            for (Object pdu : pdus) {
                SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu);
                if ("#*location*#".equals(msg.getMessageBody())) {
                    Intent intent1 = new Intent(LostFindService.this, LocationService.class);
                    startService(intent1);
                    abortBroadcast();
                }else if ("#*lock*#".equals(msg.getMessageBody())) {
                    Intent intent1 = new Intent(LostFindService.this, LockScreenService.class);
                    startService(intent1);
                    abortBroadcast();
                }else if ("#*wipe*#".equals(msg.getMessageBody())) {
                    DevicePolicyManager dm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                    dm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    abortBroadcast();
                }else if ("#*alarm*#".equals(msg.getMessageBody())) {
                    abortBroadcast();
                    MediaPlayer mediaPlayer = MediaPlayer.create(LostFindService.this, R.raw.alarm);
                    mediaPlayer.setVolume(1,1);
                    mediaPlayer.start();
                }
            }
        }
    }
}

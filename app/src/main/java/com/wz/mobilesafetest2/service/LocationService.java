package com.wz.mobilesafetest2.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.wz.mobilesafetest2.utils.SafeConstants;
import com.wz.mobilesafetest2.utils.SpTools;

public class LocationService extends Service {

    private LocationManager lm;
    private LostfindLocationListener listener;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LostfindLocationListener();
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates(bestProvider, 0, 0, listener);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        lm.removeUpdates(listener);
        lm = null;
        listener = null;
        super.onDestroy();
    }

    private class LostfindLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            float accuracy = location.getAccuracy();
            double altitude = location.getAltitude();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            float speed = location.getSpeed();

            StringBuilder sb = new StringBuilder();
            sb.append("accuracy : " + accuracy + "\n");
            sb.append("altitude : " + altitude + "\n");
            sb.append("longitude : " + longitude + "\n");
            sb.append("latitude : " + latitude + "\n");
            sb.append("speed : " + speed + "\n");
            System.out.println(sb.toString());

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(SpTools.getString(getApplicationContext(), SafeConstants.SAFENUMBER,""),
                    null,sb.toString(),null,null);
            stopSelf();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}

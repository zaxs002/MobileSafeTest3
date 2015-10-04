package com.wz.mobilesafetest2.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2015/10/1 0001.
 */
public class ServiceUtils {
    public static boolean isRunningService(Context context,String serviceName){
        boolean isRunninbg = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(50);
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (service.service.getClassName().equals(serviceName)) {
                isRunninbg = true;
                break;
            }
        }
        return isRunninbg;
    }
}

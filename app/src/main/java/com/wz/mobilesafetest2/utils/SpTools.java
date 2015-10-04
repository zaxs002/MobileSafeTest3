package com.wz.mobilesafetest2.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2015/10/1 0001.
 */
public class SpTools {
    public static void putString(Context context,String key,String value){
        SharedPreferences sp = context.getSharedPreferences(SafeConstants.SPNAME, Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }

    public static String getString(Context context,String key,String defaultValue){
        SharedPreferences sp = context.getSharedPreferences(SafeConstants.SPNAME, Context.MODE_PRIVATE);
        return sp.getString(key,defaultValue);
    }

    public static boolean getBoolean(Context context,String key,boolean defaultValue){
        SharedPreferences sp = context.getSharedPreferences(SafeConstants.SPNAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    public  static void putBoolean(Context context,String key,boolean value){
        SharedPreferences sp = context.getSharedPreferences(SafeConstants.SPNAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).commit();
    }
}

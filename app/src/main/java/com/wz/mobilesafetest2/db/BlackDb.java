package com.wz.mobilesafetest2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/10/2 0002.
 */
public class BlackDb extends SQLiteOpenHelper{
    public static final int DBVERSION = 1;
    public static final int MODE_PHONE = 1 << 0;//01;
    public static final int MODE_SMS = 1 << 1; // 10;
    public static final int MODE_ALL = MODE_PHONE | MODE_SMS;

    public static final String BLACKTB = "blacktb";
    public static final String PHONE = "phone";
    public static final String MODE = "mode";

    public BlackDb(Context context) {
        super(context, "black.db", null, DBVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacktb(_id integer primary key autoincrement,phone varchar,mode integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table blacktb");
        onCreate(db);
    }
}

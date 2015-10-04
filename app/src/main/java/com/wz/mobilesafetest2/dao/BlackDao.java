package com.wz.mobilesafetest2.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wz.mobilesafetest2.bean.BlackBean;
import com.wz.mobilesafetest2.db.BlackDb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/2 0002.
 */
public class BlackDao {
    private BlackDb helper;

    public BlackDao(Context context) {
        helper = new BlackDb(context);
    }

    public long add(String phone, int mode) {
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BlackDb.PHONE, phone);
        values.put(BlackDb.MODE, mode);
        long insert = writableDatabase.insert(BlackDb.BLACKTB, null, values);
        writableDatabase.close();
        return insert;
    }

    public void add(BlackBean bean) {
        if (bean != null) {
            add(bean.getPhone(), bean.getMode());
        }
    }

    public void delete(String phone) {
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
        writableDatabase.delete(BlackDb.BLACKTB, "phone=?", new String[]{phone});
        writableDatabase.close();
    }

    public void delete(BlackBean bean) {
        SQLiteDatabase writableDatabase = helper.getWritableDatabase();
        writableDatabase.delete(BlackDb.BLACKTB, "phone=?", new String[]{bean.getPhone()});
        writableDatabase.close();
    }

    public void update(BlackBean bean) {
        delete(bean);
        add(bean);
    }

    public List<BlackBean> getAll() {
        ArrayList<BlackBean> list = new ArrayList();
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("select " + BlackDb.PHONE + ","
                + BlackDb.MODE + " from " + BlackDb.BLACKTB + " order by _id desc", null);

        while (cursor.moveToNext()) {
            BlackBean bean = new BlackBean();
            bean.setMode(cursor.getInt(1));
            bean.setPhone(cursor.getString(0));
            list.add(bean);
        }
        cursor.close();
        return list;
    }

    public int getAllRow() {
        int rows = 0;

        Cursor cursor = helper.getReadableDatabase().rawQuery("select count(1) from " + BlackDb.BLACKTB, null);
        if (cursor.moveToNext()) {
            rows = cursor.getInt(0);
        }
        cursor.close();
        return rows;
    }

    public List<BlackBean> getPageData(int startIndex, int countsPerPage) {
        List<BlackBean> list = new ArrayList();
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
//        select phone,name from blackdb limit (startIndex - 1) * countPagePer,countPagePer;
        Cursor cursor = readableDatabase.rawQuery("select " + BlackDb.PHONE + "," + BlackDb.MODE + " from " + BlackDb.BLACKTB + " order by _id desc limit ?,?", new String[]{
                (startIndex - 1) * countsPerPage + "", countsPerPage + ""
        });
        BlackBean bean;
        while (cursor.moveToNext()) {
            bean = new BlackBean();
            bean.setPhone(cursor.getString(0));
            bean.setMode(cursor.getInt(1));
            list.add(bean);
        }
        cursor.close();
        return list;
    }

    public List<BlackBean> loadMore(int startIndex, int countsPerLoad) {
        List<BlackBean> list = new ArrayList();
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
//        select phone,name from blackdb limit (startIndex - 1) * countPagePer,countPagePer;
        Cursor cursor = readableDatabase.rawQuery("select " + BlackDb.PHONE + "," + BlackDb.MODE + " from " + BlackDb.BLACKTB + " order by _id desc limit ?,?", new String[]{
                startIndex + "", countsPerLoad + ""
        });
        BlackBean bean;
        while (cursor.moveToNext()) {
            bean = new BlackBean();
            bean.setPhone(cursor.getString(0));
            bean.setMode(cursor.getInt(1));
            list.add(bean);
        }
        cursor.close();
        return list;
    }

    public int getMode(String address) {
        int mode = 0;
        SQLiteDatabase readableDatabase = helper.getReadableDatabase();
        Cursor cursor = readableDatabase.query(BlackDb.BLACKTB, new String[]{"mode"}, "phone=?", new String[]{address}, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getInt(0);
        }
        cursor.close();
        return mode;
    }
}

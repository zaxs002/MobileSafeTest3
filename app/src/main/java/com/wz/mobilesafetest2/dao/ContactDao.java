package com.wz.mobilesafetest2.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.wz.mobilesafetest2.bean.ContactBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/10/1 0001.
 */
public class ContactDao {

    public static List<ContactBean> getContacts(Context context) {
        List<ContactBean> list = new ArrayList();
        Uri uri_contacts = Uri.parse("content://com.android.contacts/contacts");
        Uri uri_data = Uri.parse("content://com.android.contacts/data");
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri_contacts, new String[]{"_id"}, null, null, null);
        ContactBean bean = null;
        while (cursor.moveToNext()) {
            bean = new ContactBean();
            String id = cursor.getString(0);
            Cursor cursor1 = contentResolver.query(uri_data, new String[]{"data1", "mimetype"}, "raw_contact_id=?", new String[]{id}, null);
            while (cursor1.moveToNext()) {
                String data = cursor1.getString(0);
                String mimetype = cursor1.getString(1);
                if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                    bean.setPhone(data);
                } else if ("vnd.android.cursor.item/name".equals(mimetype)) {
                    bean.setName(data);
                }
            }
            cursor1.close();
            list.add(bean);
        }
        cursor.close();
        return list;
    }

    public static List<ContactBean> getSmsContacts(Context context) {
        List<ContactBean> list = new ArrayList();
        Uri uri = Uri.parse("content://sms");
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"address"}, null, null, null);

            ContactBean bean;
        while (cursor.moveToNext()) {
            bean = new ContactBean();
            bean.setName("sms");
            bean.setPhone(cursor.getString(0));
            list.add(bean);
        }
        cursor.close();
        return list;
    }

    public static List<ContactBean> getCallLogContacts(Context context) {
        ArrayList<ContactBean> list = new ArrayList();
        Uri uri = Uri.parse("content://call_log/calls");
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"name", "number"}, null, null, null);
        ContactBean contactBean;
        while (cursor.moveToNext()) {
            contactBean = new ContactBean();
            contactBean.setName(cursor.getString(0));
            contactBean.setPhone(cursor.getString(1));

            list.add(contactBean);
        }
        cursor.close();
        return list;
    }

    public static void deletelog(Context context,Uri uri, String number) {
        context.getContentResolver().delete(uri,"number=?",new String[]{number});
    }
}

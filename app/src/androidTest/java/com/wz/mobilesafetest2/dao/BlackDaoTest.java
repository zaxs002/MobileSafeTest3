package com.wz.mobilesafetest2.dao;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.wz.mobilesafetest2.bean.BlackBean;
import com.wz.mobilesafetest2.db.BlackDb;

/**
 * Created by Administrator on 2015/10/2 0002.
 */
public class BlackDaoTest extends ApplicationTestCase<Application> {

    public BlackDaoTest() {
        super(Application.class);
    }

    public void testAdd() throws Exception {
        BlackDao dao = new BlackDao(getContext());
        for(int i=0;i < 100;i++){
            dao.add("110" + i, BlackDb.MODE_PHONE);
        }
    }

    public void testQuery(){
        BlackDao dao = new BlackDao(getContext());
        for (BlackBean bean : dao.getAll()) {
            System.out.println(bean.getPhone() + "---" + bean.getMode());
        }
    }
}
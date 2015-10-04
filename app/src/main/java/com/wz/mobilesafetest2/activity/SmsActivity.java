package com.wz.mobilesafetest2.activity;

import com.wz.mobilesafetest2.bean.ContactBean;
import com.wz.mobilesafetest2.dao.ContactDao;

import java.util.List;

public class SmsActivity extends BaseSmsContactActivity {

    @Override
    public List<ContactBean> getData() {
        return ContactDao.getSmsContacts(getApplicationContext());
    }
}

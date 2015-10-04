package com.wz.mobilesafetest2.activity;

import com.wz.mobilesafetest2.bean.ContactBean;
import com.wz.mobilesafetest2.dao.ContactDao;

import java.util.List;

/**
 * Created by Administrator on 2015/10/2 0002.
 */
public class CallLogActivity extends BaseSmsContactActivity {
    @Override
    public List<ContactBean> getData() {
        return ContactDao.getCallLogContacts(getApplicationContext());
    }
}

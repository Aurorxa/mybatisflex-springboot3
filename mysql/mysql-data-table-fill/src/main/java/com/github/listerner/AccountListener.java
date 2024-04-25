package com.github.listerner;

import com.github.domain.Account;
import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;

import java.util.Date;

public class AccountListener implements InsertListener, UpdateListener {
    @Override
    public void onInsert(Object entity) {
        Account account = (Account) entity;
        account.setCreateTime(new Date());
        account.setBirthday(new Date());
    }

    @Override
    public void onUpdate(Object entity) {
        Account account = (Account) entity;
        account.setGender(2);
        account.setUpdateTime(new Date());

    }
}

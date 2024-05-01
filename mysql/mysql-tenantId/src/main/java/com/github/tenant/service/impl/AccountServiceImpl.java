package com.github.tenant.service.impl;

import com.github.tenant.domain.Account;
import com.github.tenant.mapper.AccountMapper;
import com.github.tenant.service.AccountService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account>
        implements AccountService {


}
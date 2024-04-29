package com.github.service.impl;

import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import com.github.service.AccountService;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {}

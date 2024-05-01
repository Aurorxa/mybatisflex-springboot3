package com.github.tenant.web;

import com.github.tenant.domain.Account;
import com.github.tenant.service.AccountService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController {

    @Resource
    private AccountService accountService;

    @GetMapping("/account/list")
    public List<Account> accountList() {
        return accountService.list();
    }

    @PostMapping(value = "/account/add")
    public Account add(@RequestBody Account account) {
        if (Boolean.TRUE.equals(accountService.save(account))) {
            return account;
        } else {
            return null;
        }

    }
}

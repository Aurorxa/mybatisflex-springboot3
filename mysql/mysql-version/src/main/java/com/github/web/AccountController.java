package com.github.web;

import com.github.domain.Account;
import com.github.service.AccountService;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
@RequiredArgsConstructor
public class AccountController {

    @Resource
    private AccountService accountService;

    @GetMapping("/account/list")
    public List<Account> accountList() {
        return accountService.list();
    }

    @PostMapping(value = "/add")
    public void add(List<Account> list) {
        accountService.saveBatch(list);
    }
}

package com.github.mysql.junit.basic.insert;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class BasicInsertBatchTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
    @Resource
    private AccountMapper accountMapper;

    @Test
    void testInsertBatch() {
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account()
                .setUserName("bdc")
                .setAge(18));
        accountList.add(new Account()
                .setUserName("abc")
                .setAge(19)
                .setBirthday(new Date()));
        accountList.add(new Account()
                .setUserName("Tom")
                .setAge(20));
        accountList.add(new Account()
                .setUserName("你大爷")
                .setAge(125));

        /*
           不忽略 NULL 值，即数据库中有默认值设置，也会插入 NULL，accountList 中的对象的 id 不能获取实际的值
         */
        int size = accountMapper.insertBatch(accountList);

        Assertions.assertNotNull(accountList);
        Assertions.assertNull(accountList
                .stream()
                .map(Account::getId)
                .toList()
                .get(0));
        Assertions.assertEquals(4, size);

    }
}

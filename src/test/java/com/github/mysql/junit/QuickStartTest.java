package com.github.mysql.junit;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Date;
import java.util.List;

import static com.github.domain.table.AccountTableDef.ACCOUNT;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class QuickStartTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));

    @Resource
    private AccountMapper accountMapper;

    @BeforeEach
    void setupTestData() {
        Account account = new Account();
        account.setAge(18);
        account.setUserName("张三");
        account.setBirthday(new Date());
        accountMapper.insert(account);

        Account account2 = new Account();
        account2.setAge(19);
        account2.setUserName("李四");
        account2.setBirthday(new Date());
        accountMapper.insert(account2);

        Assertions.assertNotNull(account.getId());
        Assertions.assertNotNull(account2.getId());
    }

    @AfterEach
    void cleanup() {
        List<Account> accountList = accountMapper.selectAll();

        List<Long> ids = accountList
                .stream()
                .map(Account::getId)
                .toList();

        accountMapper.deleteBatchByIds(ids);
    }

    @Test
    void testQuickStart() {
        List<Account> accountList = accountMapper.selectAll();

        log.info("ApplicationTest.testQuickStart ==> {}", accountList);

        Assertions.assertEquals(2, accountList.size());
    }

    @Test
    void testQuickQuery() {
        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select(ACCOUNT.ALL_COLUMNS)
                .from(ACCOUNT)
                .where(ACCOUNT.AGE.ge(18));

        Account account = accountMapper.selectOneByQuery(queryWrapper);

        log.info("ApplicationTest.testQuickQuery ==> {}", account);

        Assertions.assertNotNull(account);
    }

}

package com.github.mysql.junit;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Date;
import java.util.List;

import static com.github.domain.table.AccountTableDef.ACCOUNT;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
@DirtiesContext(classMode = AFTER_CLASS)
class ApplicationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));

    @Resource
    private AccountMapper accountMapper;

    @Order(1)
    @Test
    void testQuickInsert() {
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

    @Test
    @Order(1)
    void testQuickStart() {
        List<Account> accountList = accountMapper.selectAll();
        log.info("ApplicationTest.testQuickStart ==> {}", accountList);
        Assertions.assertEquals(2, accountList.size());
    }

    @Test
    @Order(2)
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

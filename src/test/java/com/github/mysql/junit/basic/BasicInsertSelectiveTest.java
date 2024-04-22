package com.github.mysql.junit.basic;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class BasicInsertSelectiveTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
    @Resource
    private AccountMapper accountMapper;

    @Test
    void testInsertSelective() {
        Account account = new Account();
        account.setUserName("abc");
        account.setAge(18);

        /*
          忽略 NULL 值，即数据库中有默认值设置，就会使用数据库中的默认值
          INSERT INTO `tb_account`(`user_name`, `age`) VALUES ('abc', 18)
        */
        accountMapper.insertSelective(account);

        Assertions.assertNotNull(account);
        Assertions.assertNotNull(account.getId());

        Account accountDb = accountMapper.selectOneById(account.getId());

        log.info("BasicInsertSelectiveTest.testInsertSelective.accountDb ==> {}", accountDb);

        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getId());
        Assertions.assertNotNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getBirthday());
        Assertions.assertNotNull(accountDb.getCreateTime());
        Assertions.assertNotNull(accountDb.getUpdateTime());
        Assertions.assertEquals(account.getUserName(), accountDb.getUserName());
        Assertions.assertEquals(account.getAge(), accountDb.getAge());
    }


}

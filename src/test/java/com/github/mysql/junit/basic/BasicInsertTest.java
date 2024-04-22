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

import java.util.Random;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class BasicInsertTest {
    
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
    // 随机数的上限
    private final Long upperBound = 100_000_000_000L;
    @Resource
    private AccountMapper accountMapper;

    @Test
    void testInsert() {
        Account account = new Account();
        account.setUserName("abc");
        account.setAge(18);

        /*
           不忽略 NULL 值，即数据库中有默认值设置，也会插入 NULL
           INSERT INTO `tb_account`(`user_name`, `age`, `birthday`, `create_time`, `update_time`)
           VALUES ('abc', 18, null, null, null)
         */
        accountMapper.insert(account);

        Assertions.assertNotNull(account);
        Assertions.assertNotNull(account.getId());

        Account accountDb = accountMapper.selectOneById(account.getId());

        log.info("BasicInsertTest.testInsert.accountDb ==> {}", accountDb);

        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getId());
        Assertions.assertNotNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getBirthday());
        Assertions.assertNull(accountDb.getCreateTime());
        Assertions.assertNull(accountDb.getUpdateTime());
    }

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

        log.info("BasicInsertTest.testInsertSelective.accountDb ==> {}", accountDb);

        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getId());
        Assertions.assertNotNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getBirthday());
        Assertions.assertNotNull(accountDb.getCreateTime());
        Assertions.assertNotNull(accountDb.getUpdateTime());
    }

    @Test
    void testInsertWithParam() {
        Account account = new Account();
        account.setUserName("abc");
        account.setAge(18);

        /*
           不忽略 NULL 值，即数据库中有默认值设置，也会插入 NULL
           INSERT INTO `tb_account`(`user_name`, `age`, `birthday`, `create_time`, `update_time`)
           VALUES ('abc', 18, null, null, null)
         */
        accountMapper.insert(account, false); // 相当于 insert()

        Assertions.assertNotNull(account);
        Assertions.assertNotNull(account.getId());

        Account accountDb = accountMapper.selectOneById(account.getId());

        log.info("BasicInsertTest.testInsertWithParam.accountDb ==> {}", accountDb);

        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getId());
        Assertions.assertNotNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getBirthday());
        Assertions.assertNull(accountDb.getCreateTime());
        Assertions.assertNull(accountDb.getUpdateTime());
    }

    @Test
    void testInsertWithParam2() {
        Account account = new Account();
        account.setUserName("abc");
        account.setAge(18);

        /*
          忽略 NULL 值，即数据库中有默认值设置，就会使用数据库中的默认值
          INSERT INTO `tb_account`(`user_name`, `age`)
          VALUES ('abc', 18)
        */
        accountMapper.insert(account, true); // 相当于 insertSelective()

        Assertions.assertNotNull(account);
        Assertions.assertNotNull(account.getId());

        Account accountDb = accountMapper.selectOneById(account.getId());

        log.info("BasicInsertTest.testInsertWithParam2.accountDb ==> {}", accountDb);

        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getId());
        Assertions.assertNotNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getBirthday());
        Assertions.assertNotNull(accountDb.getCreateTime());
        Assertions.assertNotNull(accountDb.getUpdateTime());
    }

    @Test
    void testInsertWithPk() {
        Account account = new Account();
        account.setId(Math.abs(new Random().nextLong()));
        account.setUserName("abc");
        account.setAge(18);

        /*
           不忽略 NULL 值，即数据库中有默认值设置，也会插入 NULL
           INSERT INTO `tb_account`(`user_name`, `age`, `birthday`, `create_time`, `update_time`)
           VALUES ('abc', 18, null, null, null)
         */
        accountMapper.insertWithPk(account);

        Assertions.assertNotNull(account);
        Assertions.assertNotNull(account.getId());

        Account accountDb = accountMapper.selectOneById(account.getId());

        log.info("BasicInsertTest.testInsertWithPk.accountDb ==> {}", accountDb);

        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getId());
        Assertions.assertNotNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getBirthday());
        Assertions.assertNull(accountDb.getCreateTime());
        Assertions.assertNull(accountDb.getUpdateTime());
    }

    @Test
    void testInsertSelectiveWithPk() {
        Account account = new Account();
        account.setId(Math.abs(new Random().nextLong()) % upperBound);
        account.setUserName("abc");
        account.setAge(18);

        /*
          忽略 NULL 值，即数据库中有默认值设置，就会使用数据库中的默认值
          INSERT INTO `tb_account`(`user_name`, `age`)
          VALUES ('abc', 18)
        */
        accountMapper.insertSelectiveWithPk(account);

        Assertions.assertNotNull(account);
        Assertions.assertNotNull(account.getId());

        Account accountDb = accountMapper.selectOneById(account.getId());

        log.info("BasicInsertTest.testInsertSelectiveWithPk.accountDb ==> {}", accountDb);

        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getId());
        Assertions.assertNotNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getBirthday());
        Assertions.assertNotNull(accountDb.getCreateTime());
        Assertions.assertNotNull(accountDb.getUpdateTime());
    }

    @Test
    void testInsertWithPkWithParams() {
        Account account = new Account();
        account.setId(Math.abs(new Random().nextLong()));
        account.setUserName("abc");
        account.setAge(18);

        /*
           不忽略 NULL 值，即数据库中有默认值设置，也会插入 NULL
           INSERT INTO `tb_account`(`user_name`, `age`, `birthday`, `create_time`, `update_time`)
           VALUES ('abc', 18, null, null, null)
         */
        accountMapper.insertWithPk(account, false); // 相当于 insertWithPk()

        Assertions.assertNotNull(account);
        Assertions.assertNotNull(account.getId());

        Account accountDb = accountMapper.selectOneById(account.getId());

        log.info("BasicInsertTest.testInsertWithPkWithParams.accountDb ==> {}", accountDb);

        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getId());
        Assertions.assertNotNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getBirthday());
        Assertions.assertNull(accountDb.getCreateTime());
        Assertions.assertNull(accountDb.getUpdateTime());
    }

    @Test
    void testInsertWithPkWithParams2() {
        Account account = new Account();
        account.setId(Math.abs(new Random().nextLong()) % upperBound);
        account.setUserName("abc");
        account.setAge(18);

        /*
          忽略 NULL 值，即数据库中有默认值设置，就会使用数据库中的默认值
          INSERT INTO `tb_account`(`user_name`, `age`)
          VALUES ('abc', 18)
        */
        accountMapper.insertWithPk(account, true); // 相当于 insertSelectiveWithPk()

        Assertions.assertNotNull(account);
        Assertions.assertNotNull(account.getId());

        Account accountDb = accountMapper.selectOneById(account.getId());

        log.info("BasicInsertTest.testInsertWithPkWithParams2.accountDb ==> {}", accountDb);

        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getId());
        Assertions.assertNotNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getBirthday());
        Assertions.assertNotNull(accountDb.getCreateTime());
        Assertions.assertNotNull(accountDb.getUpdateTime());
    }

}

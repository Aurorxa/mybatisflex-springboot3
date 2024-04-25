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

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class BasicInsertOrUpdateSelectiveTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
    @Resource
    private AccountMapper accountMapper;

    @Test
    void testInsertOrUpdateSelective() {
        Account account = new Account();
        account.setUserName("abc");
        account.setAge(18);

        /*
           忽略 NULL 值，即数据库中有默认值设置，就使用数据库中的值

           INSERT INTO `tb_account`(`user_name`, `age`)
           VALUES ('abc', 18)
         */
        accountMapper.insertOrUpdateSelective(account);

        Assertions.assertNotNull(account);
        Assertions.assertNotNull(account.getId());

        Account accountDb = accountMapper.selectOneById(account.getId());

        log.info("BasicInsertOrUpdateTest.testInsertOrUpdateSelective.accountDb ==> {}", accountDb);

        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getId());
        Assertions.assertNotNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getBirthday());
        Assertions.assertNotNull(accountDb.getCreateTime());
        Assertions.assertNotNull(accountDb.getUpdateTime());
        Assertions.assertEquals(account.getUserName(), accountDb.getUserName());
        Assertions.assertEquals(account.getAge(), accountDb.getAge());

        /*
         * 因为有主键，所以此处是更新
         * UPDATE `tb_account`
         * SET `user_name` = 'abc' , `age` = 18 , `create_time` = ? , `update_time` = ?
         * WHERE `id` = 1
         */
        accountMapper.insertOrUpdateSelective(accountDb);

        Account accountDb2 = accountMapper.selectOneById(accountDb.getId());
        log.info("BasicInsertOrUpdateTest.testInsertOrUpdateSelective.accountDb2 ==> {}", accountDb2);

        Assertions.assertNotNull(accountDb2);
        Assertions.assertNotNull(accountDb2.getId());
        Assertions.assertNotNull(accountDb2.getUserName());
        Assertions.assertNotNull(accountDb2.getAge());
        Assertions.assertNull(accountDb2.getBirthday());
        Assertions.assertNotNull(accountDb2.getCreateTime());
        Assertions.assertNotNull(accountDb2.getUpdateTime());
        Assertions.assertEquals(accountDb2.getUserName(), accountDb.getUserName());
        Assertions.assertEquals(accountDb2.getAge(), accountDb.getAge());
    }

}

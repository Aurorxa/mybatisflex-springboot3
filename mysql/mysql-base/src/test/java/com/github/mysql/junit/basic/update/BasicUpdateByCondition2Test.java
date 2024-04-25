package com.github.mysql.junit.basic.update;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import com.mybatisflex.core.query.QueryCondition;
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

import java.util.Date;

import static com.github.domain.table.AccountTableDef.ACCOUNT;

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class BasicUpdateByCondition2Test {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
    @Resource
    private AccountMapper accountMapper;

    @Test
    void testUpdateByCondition() {
        Account account = new Account();
        account.setUserName("abc");

        // 新增，会忽略 NULL 值
        int size = accountMapper.insertSelective(account);
        Assertions.assertEquals(1, size);
        Assertions.assertNotNull(account.getId());

        Account account2 = new Account();
        account2.setUserName("bcd");
        account2.setAge(18);

        // 新增，会忽略 NULL 值
        int size2 = accountMapper.insertSelective(account2);
        Assertions.assertEquals(1, size2);
        Assertions.assertNotNull(account2.getId());

        /*
            更新数据

            UPDATE `tb_account`
            SET `user_name` = null , `age` = null , `birthday` = ? , `create_time` = null , `update_time` = null
            WHERE `age` = 18
         */
        QueryCondition queryCondition = ACCOUNT.AGE.eq(18);
        int size3 = accountMapper.updateByCondition(new Account().setBirthday(new Date()), false, queryCondition);
        Assertions.assertEquals(1, size3);

        // 查询数据
        Account accountDb = accountMapper.selectOneById(account2.getId());
        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getBirthday());
        Assertions.assertNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getCreateTime());
        Assertions.assertNull(accountDb.getUpdateTime());
    }


}

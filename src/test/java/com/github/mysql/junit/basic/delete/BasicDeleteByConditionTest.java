package com.github.mysql.junit.basic.delete;

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

import java.util.Random;

import static com.github.domain.table.AccountTableDef.ACCOUNT;

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class BasicDeleteByConditionTest {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
    @Resource
    private AccountMapper accountMapper;

    @Test
    void testDeleteByCondition() {
        // 新增数据
        Account account = new Account();
        account.setId(Math.abs(new Random().nextLong()));
        account.setUserName("abc");
        account.setAge(18);
        accountMapper.insert(account);

        // 查询数据
        Account accountDb = accountMapper.selectOneById(account.getId());
        Assertions.assertNotNull(accountDb);

        /*
          删除数据，这种方式可以组合任意条件
          DELETE FROM `tb_account` WHERE `user_name` = ? AND `age` = ?
         */
        QueryCondition queryCondition = ACCOUNT.USER_NAME
                .eq("abc")
                .and(ACCOUNT.AGE.eq(18));
        int size = accountMapper.deleteByCondition(queryCondition);
        Assertions.assertEquals(1, size);

        // 查询数据
        accountDb = accountMapper.selectOneById(account.getId());
        Assertions.assertNull(accountDb);
    }

}

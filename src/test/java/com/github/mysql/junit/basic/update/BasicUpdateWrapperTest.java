package com.github.mysql.junit.basic.update;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import com.mybatisflex.core.update.UpdateWrapper;
import com.mybatisflex.core.util.UpdateEntity;
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

import static com.github.domain.table.AccountTableDef.ACCOUNT;

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class BasicUpdateWrapperTest {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
    @Resource
    private AccountMapper accountMapper;

    @Test
    void testUpdateWrapper() {
        Account account = new Account();
        account.setUserName("abc");
        account.setAge(18);
        // 新增，会忽略 NULL 值
        int size = accountMapper.insertSelective(account);
        Assertions.assertEquals(1, size);
        Assertions.assertNotNull(account);
        Assertions.assertNotNull(account.getId());

        // 部分字段更新，即更新的字段中，希望一些可以为 NULL，一些可以不为 NULL，即开发者自行选择
        Account account2 = UpdateEntity.of(Account.class, account.getId());
        account2.setUserName(null);
        account2.setAge(10);
        /*
          在以上的部分字段更新中，只能更新为用户传入的数据，但是有些时候我们想更新为数据库计算的数据

          UPDATE `tb_account`
          SET `user_name` = ? , `age` = age + 1
          WHERE `id` = ?
        */
        UpdateWrapper<Account> updateWrapper = UpdateWrapper.of(account2);
        updateWrapper.set(ACCOUNT.AGE, ACCOUNT.AGE.add(1));
        size = accountMapper.update(account2);
        Assertions.assertEquals(1, size);

        // 查询数据
        Account accountDb = accountMapper.selectOneById(account.getId());
        Assertions.assertNotNull(accountDb);
        Assertions.assertNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getBirthday());
        Assertions.assertNotNull(accountDb.getCreateTime());
        Assertions.assertNotNull(accountDb.getUpdateTime());
    }
}

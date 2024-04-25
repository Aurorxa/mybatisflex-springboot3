package com.github.mysql.junit.basic.delete;

import com.github.Application;
import com.github.domain.Account;
import com.github.domain.table.AccountTableDef;
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

import java.util.Map;
import java.util.Random;

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class BasicDeleteByMapTest {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
    @Resource
    private AccountMapper accountMapper;

    @Test
    void testDeleteByMap() {
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
          删除数据，这种方式只能用于 = ，需要注意的是，这种方式需要拼接实际数据库的字段名，而非实体类的属性名

          DELETE FROM `tb_account`
          WHERE `tb_account`.`user_name` = 'abc'
         */
        Map<String, Object> whereCondition = Map.of(AccountTableDef.ACCOUNT.USER_NAME.getName(), "abc");
        int size = accountMapper.deleteByMap(whereCondition);
        Assertions.assertEquals(1, size);

        // 查询数据
        accountDb = accountMapper.selectOneById(account.getId());
        Assertions.assertNull(accountDb);
    }

}

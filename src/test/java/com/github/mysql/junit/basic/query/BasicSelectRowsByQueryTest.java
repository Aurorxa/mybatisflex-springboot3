package com.github.mysql.junit.basic.query;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Row;
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
import java.util.List;

import static com.github.domain.table.AccountTableDef.ACCOUNT;

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class BasicSelectRowsByQueryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
    @Resource
    private AccountMapper accountMapper;

    @Test
    void testSelectRowsByQuery() {
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account()
                .setUserName("a")
                .setAge(17));
        accountList.add(new Account()
                .setUserName("b")
                .setAge(20));
        accountList.add(new Account()
                .setUserName("c")
                .setAge(20));
        accountList.add(new Account()
                .setUserName("d")
                .setAge(20));
        accountList.add(new Account()
                .setUserName("e")
                .setAge(22));
        accountList.add(new Account()
                .setUserName("f")
                .setAge(22));
        accountList.add(new Account()
                .setUserName("g")
                .setAge(20));
        accountList.add(new Account()
                .setUserName("h")
                .setAge(20));

        int size = accountMapper.insertBatch(accountList);
        log.info("BasicSelectRowsByQueryTest.testSelectRowsByQuery.size ==> {}", size);

        /*
         * 查询数据
         *
         * SELECT `id`, `user_name`, `age`, `birthday`, `create_time`, `update_time`
         * FROM `tb_account`
         * WHERE `age` >= ?
         */

        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .where(ACCOUNT.AGE.ge(20));

        List<Row> rows = accountMapper.selectRowsByQuery(queryWrapper);

        log.info("BasicSelectRowsByQueryTest.testSelectRowsByQuery.rows ==> {}", rows);

        Assertions.assertNotNull(rows);
        Assertions.assertNotNull(rows
                .get(0)
                .getLong(ACCOUNT.ID.getName()));
    }


}

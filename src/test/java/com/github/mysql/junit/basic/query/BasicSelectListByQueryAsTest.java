package com.github.mysql.junit.basic.query;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import com.github.vo.AccountVo;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
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
class BasicSelectListByQueryAsTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
    @Resource
    private AccountMapper accountMapper;

    @Test
    void testSelectListByQueryAs() {
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
        log.info("BasicSelectListByQueryAsTest.testSelectListByQueryAs.size ==> {}", size);

        /*
         * 查询数据
         *
         * SELECT *, IF(`age` >= ?, TRUE, FALSE) AS `is_audit`
         * FROM `tb_account`
         * WHERE `user_name` IN (?, ?, ?, ?, ?)
         */

        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select(ACCOUNT.ALL_COLUMNS, QueryMethods
                        .if_(ACCOUNT.AGE.ge(18), QueryMethods.true_(), QueryMethods.false_())
                        .as("is_audit"))
                .where(ACCOUNT.USER_NAME.in("a", "b", "c", "d", "e"));

        List<AccountVo> accountVoList = accountMapper.selectListByQueryAs(queryWrapper, AccountVo.class);

        log.info("BasicSelectListByQueryAsTest.testSelectListByQueryAs.accountDbList ==> {}", accountVoList);

        Assertions.assertNotNull(accountVoList);
        Assertions.assertEquals(5, accountVoList.size());
    }


}

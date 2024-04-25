package com.github.mysql.junit.basic.query;

import com.github.Application;
import com.github.domain.Account;
import com.github.domain.table.AccountTableDef;
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

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class BasicSelectListByIdsTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
    @Resource
    private AccountMapper accountMapper;

    @Test
    void testSelectListByIds() {
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account()
                .setUserName("a")
                .setAge(17));
        accountList.add(new Account()
                .setUserName("b")
                .setAge(19));
        accountList.add(new Account()
                .setUserName("c")
                .setAge(20));
        accountList.add(new Account()
                .setUserName("d")
                .setAge(21));
        accountList.add(new Account()
                .setUserName("e")
                .setAge(22));

        accountMapper.insertBatch(accountList);

        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select(AccountTableDef.ACCOUNT.ALL_COLUMNS, QueryMethods
                        .if_(AccountTableDef.ACCOUNT.AGE.ge(18), QueryMethods.true_(), QueryMethods.false_())
                        .as("is_audit"))
                .where(AccountTableDef.ACCOUNT.USER_NAME.in("a", "b", "c", "d", "e"));
        /*
         * 查询数据
         *
         * SELECT *, IF(`age` >= ?, TRUE, FALSE) AS `is_audit`
         * FROM `tb_account`
         * WHERE `user_name` IN (?, ?, ?, ?, ?)
         */
        List<AccountVo> accountVoList = accountMapper.selectListByQueryAs(queryWrapper, AccountVo.class);

        log.info("BasicSelectOneByEntityIdTest.testSelectOneByEntityId.accountVoList ==> {}", accountVoList);

        Assertions.assertNotNull(accountVoList);
        Assertions.assertEquals(accountList.size(), accountVoList.size());


        List<Long> idList = accountVoList
                .stream()
                .map(AccountVo::getId)
                .toList();
        List<Account> accountDbList = accountMapper.selectListByIds(idList);

        log.info("BasicSelectOneByEntityIdTest.testSelectOneByEntityId.accountDbList ==> {}", accountDbList);

        Assertions.assertNotNull(accountDbList);
        Assertions.assertEquals(accountList.size(), accountDbList.size());

    }


}

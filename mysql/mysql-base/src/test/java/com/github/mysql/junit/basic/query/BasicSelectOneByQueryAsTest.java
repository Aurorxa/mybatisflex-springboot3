package com.github.mysql.junit.basic.query;

import static com.github.domain.table.AccountTableDef.ACCOUNT;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import com.github.service.AccountService;
import com.github.vo.AccountVo;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class BasicSelectOneByQueryAsTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private AccountService accountService;

    private StopWatch stopWatch;

    private TestInfo currentTestInfo;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        stopWatch = new StopWatch();
        stopWatch.start();
        currentTestInfo = testInfo;
    }

    @AfterEach
    public void tearDown() {
        stopWatch.stop();
        log.info(
                "Test 方法：{}  execution time: {} ms ",
                Objects.requireNonNull(currentTestInfo.getTestMethod().orElse(null))
                        .getName(),
                stopWatch.getTotalTimeMillis());
    }

    @Test
    void testSelectOneByQueryAs() {
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

        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(
                        ACCOUNT.ALL_COLUMNS,
                        QueryMethods.if_(ACCOUNT.AGE.ge(18), QueryMethods.true_(), QueryMethods.false_())
                                .as("is_audit"))
                .where(ACCOUNT.USER_NAME.eq("abc"));
        /*
         * 查询数据
         *
         * SELECT *, IF(`age` >= ?, TRUE, FALSE) AS `is_audit`
         * FROM `tb_account`
         * WHERE `user_name` = ? LIMIT 1
         */
        AccountVo accountVo = accountMapper.selectOneByQueryAs(queryWrapper, AccountVo.class);

        log.info("BasicSelectOneByQueryTest.testSelectOneByQueryAs.accountVo ==> {}", accountVo);

        Assertions.assertNotNull(accountVo);
        Assertions.assertNotNull(accountVo.getId());
        Assertions.assertNotNull(accountVo.getUserName());
        Assertions.assertNull(accountVo.getBirthday());
        Assertions.assertNull(accountVo.getCreateTime());
        Assertions.assertEquals(account.getUserName(), accountVo.getUserName());
        Assertions.assertTrue(accountVo.getIsAudit());
    }

    @Test
    void testSelectOneByQueryAs2() {
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account().setUserName("a").setAge(17).setMoney(new BigDecimal(100)));
        accountList.add(new Account().setUserName("b").setAge(19).setMoney(new BigDecimal("100.00")));
        accountList.add(new Account().setUserName("c").setAge(20).setMoney(new BigDecimal("102.00")));
        accountList.add(new Account().setUserName("d").setAge(21).setMoney(new BigDecimal("100.12")));
        accountList.add(new Account().setUserName("e").setAge(22).setMoney(new BigDecimal("100.15")));

        accountMapper.insertBatch(accountList);

        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(
                        ACCOUNT.ALL_COLUMNS,
                        QueryMethods.if_(ACCOUNT.AGE.ge(18), QueryMethods.true_(), QueryMethods.false_())
                                .as("is_audit"))
                .where(ACCOUNT.USER_NAME.in("a", "b", "c", "d", "e"));
        /*
         * 查询数据
         *
         * SELECT *, IF(`age` >= ?, TRUE, FALSE) AS `is_audit`
         * FROM `tb_account`
         * WHERE `user_name` IN (?, ?, ?, ?, ?) LIMIT 1
         */
        AccountVo accountVo = accountMapper.selectOneByQueryAs(queryWrapper, AccountVo.class);

        AccountVo objAs = accountService.getOneAs(queryWrapper, AccountVo.class);

        log.info("objAs => {} ", objAs);

        log.info("BasicSelectOneByQueryTest.testSelectOneByQueryAs2.accountVo ==> {}", accountVo);

        Assertions.assertNotNull(accountVo);
        Assertions.assertNotNull(accountVo.getId());
        Assertions.assertNotNull(accountVo.getUserName());
        Assertions.assertNull(accountVo.getBirthday());
        Assertions.assertNull(accountVo.getCreateTime());
    }
}

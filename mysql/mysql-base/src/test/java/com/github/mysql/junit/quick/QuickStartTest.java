package com.github.mysql.junit.quick;

import com.github.Application;
import com.github.domain.Account;
import com.github.domain.table.AccountTableDef;
import com.github.mapper.AccountMapper;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import java.util.Date;
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
class QuickStartTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));

    @Resource
    private AccountMapper accountMapper;

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

    @BeforeEach
    void setupTestData() {
        Account account = new Account();
        account.setAge(18);
        account.setUserName("张三");
        account.setBirthday(new Date());
        accountMapper.insert(account);

        Account account2 = new Account();
        account2.setAge(19);
        account2.setUserName("李四");
        account2.setBirthday(new Date());
        accountMapper.insert(account2);

        Assertions.assertNotNull(account.getId());
        Assertions.assertNotNull(account2.getId());
    }

    @AfterEach
    void cleanup() {
        List<Account> accountList = accountMapper.selectAll();

        List<Long> ids = accountList.stream().map(Account::getId).toList();

        accountMapper.deleteBatchByIds(ids);
    }

    @Test
    void testQuickStart() {
        List<Account> accountList = accountMapper.selectAll();

        log.info("ApplicationTest.testQuickStart ==> {}", accountList);

        Assertions.assertEquals(2, accountList.size());
    }

    @Test
    void testQuickQuery() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(AccountTableDef.ACCOUNT.ALL_COLUMNS)
                .from(AccountTableDef.ACCOUNT)
                .where(AccountTableDef.ACCOUNT.AGE.ge(18));

        Account account = accountMapper.selectOneByQuery(queryWrapper);

        log.info("ApplicationTest.testQuickQuery ==> {}", account);

        Assertions.assertNotNull(account);
    }
}

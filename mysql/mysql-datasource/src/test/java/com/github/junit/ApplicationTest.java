package com.github.junit;

import static com.github.domain.table.AccountTableDef.ACCOUNT;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import com.github.service.AccountService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import jakarta.annotation.Resource;
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
class ApplicationTest {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));

    private StopWatch stopWatch;

    private TestInfo currentTestInfo;

    @Resource
    private AccountService accountService;

    @Resource
    private AccountMapper accountMapper;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        stopWatch = new StopWatch();
        stopWatch.start();
        currentTestInfo = testInfo;

        // 准备数据
        List<Account> accountList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Account account = new Account();
            account.setUserName("userName" + i);
            account.setAge(i);
            accountList.add(account);
        }

        accountService.saveBatch(accountList, 500);
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
    void test1() {
        List<Account> accountList = accountService
                .queryChain()
                .select(ACCOUNT.DEFAULT_COLUMNS)
                .from(ACCOUNT)
                .where(ACCOUNT.AGE.ge(100))
                .list();

        log.info("ApplicationTest.test1.accountList ==> {}", accountList);

        Assertions.assertNotNull(accountList);
    }

    @Test
    void test2() {
        List<Account> accountList = QueryChain.of(accountMapper)
                .select(ACCOUNT.DEFAULT_COLUMNS)
                .from(ACCOUNT)
                .where(ACCOUNT.AGE.ge(100))
                .list();

        log.info("ApplicationTest.test2.accountList ==> {}", accountList);

        Assertions.assertNotNull(accountList);
    }

    @Test
    void test3() {
        List<Account> accountList = accountService
                .queryChain()
                .select(ACCOUNT.DEFAULT_COLUMNS)
                .from(ACCOUNT)
                .where(ACCOUNT.AGE.ge(100))
                .withRelations()
                .list();

        log.info("ApplicationTest.test3.accountList ==> {}", accountList);

        Assertions.assertNotNull(accountList);
    }

    @Test
    void test4() {
        boolean flag = UpdateChain.of(Account.class)
                .set(Account::getUserName, "张三")
                .setRaw(Account::getAge, "age + 1")
                .where(Account::getUserName)
                .eq("userName999")
                .update();

        Assertions.assertTrue(flag);
    }
}

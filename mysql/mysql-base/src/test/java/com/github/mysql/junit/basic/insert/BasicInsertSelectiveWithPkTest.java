package com.github.mysql.junit.basic.insert;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import jakarta.annotation.Resource;
import java.util.Objects;
import java.util.Random;
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
class BasicInsertSelectiveWithPkTest {

    private static final long upperBound = 100_000_000_000L;

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

    @Test
    void testInsertSelectiveWithPk() {
        Account account = new Account();
        // 随机数的上限
        account.setId(Math.abs(new Random().nextLong()) % upperBound);
        account.setUserName("abc");
        account.setAge(18);

        /*
          忽略 NULL 值，即数据库中有默认值设置，就会使用数据库中的默认值

          INSERT INTO `tb_account`(`user_name`, `age`)
          VALUES ('abc', 18)
        */
        accountMapper.insertSelectiveWithPk(account);

        Assertions.assertNotNull(account);
        Assertions.assertNotNull(account.getId());

        Account accountDb = accountMapper.selectOneById(account.getId());

        log.info("BasicInsertSelectiveWithPkTest.testInsertSelectiveWithPk.accountDb ==> {}", accountDb);

        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getId());
        Assertions.assertNotNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getBirthday());
        Assertions.assertNotNull(accountDb.getCreateTime());
        Assertions.assertNotNull(accountDb.getUpdateTime());
        Assertions.assertEquals(account.getUserName(), accountDb.getUserName());
        Assertions.assertEquals(account.getAge(), accountDb.getAge());
    }
}

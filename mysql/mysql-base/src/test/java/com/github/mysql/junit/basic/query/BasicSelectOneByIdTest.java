package com.github.mysql.junit.basic.query;

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
class BasicSelectOneByIdTest {

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
    void testSelectOneById() {
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

        /*
         * 查询数据
         *
         * SELECT *
         * FROM `tb_account`
         * WHERE `id` = ?
         */
        Account accountDb = accountMapper.selectOneById(account.getId());

        log.info("BasicSelectOneByIdTest.testSelectOneById.accountDb ==> {}", accountDb);

        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getId());
        Assertions.assertNotNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getAge());
        Assertions.assertNull(accountDb.getBirthday());
        Assertions.assertNull(accountDb.getCreateTime());
        Assertions.assertNull(accountDb.getUpdateTime());
        Assertions.assertEquals(account.getUserName(), accountDb.getUserName());
        Assertions.assertEquals(account.getAge(), accountDb.getAge());
    }

    @Test
    void testSelectOneById2() {
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

        Long randomNumber = new Random()
                .longs(1, Long.MAX_VALUE)
                .filter(n -> n != account.getId())
                .limit(1)
                .sum();
        /*
         * 查询数据
         * SELECT *
         * FROM `tb_account`
         * WHERE `id` = ?
         */
        Account accountDb = accountMapper.selectOneById(randomNumber);

        log.info("BasicSelectOneByIdTest.testSelectOneById2.accountDb ==> {}", accountDb);

        Assertions.assertNull(accountDb);
    }
}

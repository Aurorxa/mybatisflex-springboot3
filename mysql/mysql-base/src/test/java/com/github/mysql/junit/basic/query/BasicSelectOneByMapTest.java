package com.github.mysql.junit.basic.query;

import static com.github.domain.table.AccountTableDef.ACCOUNT;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import jakarta.annotation.Resource;
import java.util.Map;
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
class BasicSelectOneByMapTest {

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
    void testSelectOneByMap() {
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
         * SELECT `id`, `user_name`, `age`, `birthday`, `create_time`, `update_time`
         * FROM `tb_account`
         * WHERE `tb_account`.`user_name` = ? LIMIT 1
         */
        Map<String, Object> whereConditions = Map.of(ACCOUNT.USER_NAME.getName(), "abc");
        Account accountDb = accountMapper.selectOneByMap(whereConditions);

        log.info("BasicSelectOneByMapTest.testSelectOneByMap.accountDb ==> {}", accountDb);

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
    void testSelectOneByMap2() {
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
         * SELECT `id`, `user_name`, `age`, `birthday`, `create_time`, `update_time`
         * FROM `tb_account`
         * WHERE `tb_account`.`user_name` = ? AND `tb_account`.`age` = ? LIMIT 1
         */
        Map<String, Object> whereConditions = Map.of(ACCOUNT.USER_NAME.getName(), "abc", ACCOUNT.AGE.getName(), 19);
        Account accountDb = accountMapper.selectOneByMap(whereConditions);

        log.info("BasicSelectOneByMapTest.testSelectOneByMap2.accountDb ==> {}", accountDb);

        Assertions.assertNull(accountDb);
    }
}

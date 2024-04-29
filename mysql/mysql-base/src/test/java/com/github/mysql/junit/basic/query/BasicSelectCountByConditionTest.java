package com.github.mysql.junit.basic.query;

import com.github.Application;
import com.github.domain.Account;
import com.github.domain.table.AccountTableDef;
import com.github.mapper.AccountMapper;
import com.mybatisflex.core.query.QueryCondition;
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
class BasicSelectCountByConditionTest {

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
    void testSelectCountByCondition() {
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account().setUserName("a").setAge(17));
        accountList.add(new Account().setUserName("b").setAge(20));
        accountList.add(new Account().setUserName("c").setAge(20));
        accountList.add(new Account().setUserName("d").setAge(20));
        accountList.add(new Account().setUserName("e").setAge(22));
        accountList.add(new Account().setUserName("f").setAge(22));
        accountList.add(new Account().setUserName("g").setAge(20));
        accountList.add(new Account().setUserName("h").setAge(20));

        int size = accountMapper.insertBatch(accountList);
        log.info("BasicSelectCountByConditionTest.testSelectCountByCondition.size ==> {}", size);

        /*
         * 查询数据
         *
         * SELECT `age`
         * FROM `tb_account`
         * WHERE `age` >= ?
         */

        QueryCondition queryCondition = AccountTableDef.ACCOUNT.AGE.ge(20);

        long count = accountMapper.selectCountByCondition(queryCondition);

        log.info("BasicSelectCountByConditionTest.testSelectCountByCondition.count==> {}", count);

        Assertions.assertEquals(7, count);
    }
}

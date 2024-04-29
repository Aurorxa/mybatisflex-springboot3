package com.github;

import com.github.domain.Account;
import com.github.enums.AccountGradeEnum;
import com.github.mapper.AccountMapper;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
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
    private AccountMapper accountMapper;

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
    void testVersion() {
        Account account = new Account();
        account.setUserName("abc");

        int size = accountMapper.insertSelective(account);
        Assertions.assertEquals(1, size);

        // 查询
        Account accountDb = accountMapper.selectOneById(account.getId());
        accountDb.setGrade(AccountGradeEnum.EXCELLENT);

        // 更新
        size = accountMapper.update(accountDb);
        Assertions.assertEquals(1, size);

        // 查询
        accountDb = accountMapper.selectOneById(account.getId());
        accountDb.setMoney(new BigDecimal("200"));

        // 更新
        size = accountMapper.update(accountDb);
        Assertions.assertEquals(1, size);

        // 查询
        accountDb = accountMapper.selectOneById(account.getId());
        Assertions.assertNotNull(accountDb);
    }
}

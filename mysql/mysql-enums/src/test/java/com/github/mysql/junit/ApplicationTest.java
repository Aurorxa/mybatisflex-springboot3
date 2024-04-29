package com.github.mysql.junit;

import com.github.Application;
import com.github.domain.Account;
import com.github.enums.AccountGradeEnum;
import com.github.mapper.AccountMapper;
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
    void testEnums() {
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account().setUserName("a").setPassword("abc").setGrade(AccountGradeEnum.EXCELLENT));
        accountList.add(new Account().setUserName("b").setPassword("123").setGrade(AccountGradeEnum.PASS));
        accountList.add(new Account().setUserName("c").setPassword("234").setGrade(AccountGradeEnum.POOR));

        accountMapper.insertBatch(accountList);

        List<Account> accountDbList = accountMapper.selectAll();
        Assertions.assertEquals(3, accountDbList.size());
        Assertions.assertNotNull(accountDbList.get(0).getGrade());
    }
}

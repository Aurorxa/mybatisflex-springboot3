package com.github.junit;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import com.mybatisflex.core.row.BatchArgsSetter;
import com.mybatisflex.core.row.Db;
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
    void testInsertBatch() {
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account().setUserName("a"));
        accountList.add(new Account().setUserName("b"));
        accountList.add(new Account().setUserName("c"));
        accountList.add(new Account().setUserName("d"));
        accountList.add(new Account().setUserName("e"));

        int size = accountMapper.insertBatch(accountList);

        log.info("ApplicationTest.testInsertBatch.size ==> {}", size);

        Assertions.assertEquals(accountList.size(), size);
    }

    @Test
    void testExecuteUpdate() {
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account().setUserName("a"));
        accountList.add(new Account().setUserName("b"));
        accountList.add(new Account().setUserName("c"));
        accountList.add(new Account().setUserName("d"));
        accountList.add(new Account().setUserName("e"));

        Db.executeBatch(accountList, AccountMapper.class, AccountMapper::insertSelective);

        log.info("ApplicationTest.testExecuteUpdate.accountList ==> {}", accountList);

        Assertions.assertNotNull(accountList.get(0).getId());
    }

    @Test
    void testUpdateBatch() {
        String sql = """
           UPDATE tb_account SET user_name = ? WHERE id = ?
           """;

        List<Object[]> paramsList = new ArrayList<>();
        paramsList.add(new Object[] {"a", 1L});
        paramsList.add(new Object[] {"b", 2L});
        paramsList.add(new Object[] {"c", 3L});

        int[] intArr = Db.updateBatch(sql, new BatchArgsSetter() {
            @Override
            public int getBatchSize() {
                return paramsList.size();
            }

            @Override
            public Object[] getSqlArgs(int index) {
                return paramsList.get(index);
            }
        });

        Assertions.assertNotNull(intArr);

        Assertions.assertEquals(3, intArr.length);

        log.info("ApplicationTest.testUpdateBatch.ints ==> {}", intArr);
    }
}

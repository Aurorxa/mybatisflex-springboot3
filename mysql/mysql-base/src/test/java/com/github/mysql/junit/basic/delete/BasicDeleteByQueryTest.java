package com.github.mysql.junit.basic.delete;

import com.github.Application;
import com.github.domain.Account;
import com.github.domain.table.AccountTableDef;
import com.github.mapper.AccountMapper;
import com.mybatisflex.core.query.QueryWrapper;
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
class BasicDeleteByQueryTest {
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
    void testDeleteByQuery() {
        // 新增数据
        Account account = new Account();
        account.setId(Math.abs(new Random().nextLong()));
        account.setUserName("abc");
        account.setAge(18);
        accountMapper.insert(account);

        // 查询数据
        Account accountDb = accountMapper.selectOneById(account.getId());
        Assertions.assertNotNull(accountDb);

        /*
         删除数据，这种方式可以组合任意条件；不过，QueryWrapper 多用于查询

         DELETE FROM `tb_account`
         WHERE `user_name` = ? AND `age` = ?
        */
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(AccountTableDef.ACCOUNT.ALL_COLUMNS)
                .from(AccountTableDef.ACCOUNT)
                .where(AccountTableDef.ACCOUNT.USER_NAME.eq("abc"))
                .and(AccountTableDef.ACCOUNT.AGE.eq(18));
        int size = accountMapper.deleteByQuery(queryWrapper);
        Assertions.assertEquals(1, size);

        // 查询数据
        accountDb = accountMapper.selectOneById(account.getId());
        Assertions.assertNull(accountDb);
    }
}

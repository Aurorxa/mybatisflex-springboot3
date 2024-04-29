package com.github.mysql.junit.basic.update;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import jakarta.annotation.Resource;
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
class BasicUpdate2Test {

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
    void testUpdate() {
        Account account = new Account();
        account.setUserName("abc");
        account.setAge(18);

        // 新增，会忽略 NULL 值
        int size = accountMapper.insertSelective(account);
        Assertions.assertEquals(1, size);
        Assertions.assertNotNull(account);
        Assertions.assertNotNull(account.getId());

        Account account2 = new Account();
        account2.setId(account.getId());
        account2.setUserName("bcd");

        /*
         * 修改，不忽略 NULL 值，即属性为 NULL 的数据会更新到数据库中
         *
         * UPDATE `tb_account`
         * SET `user_name` = 'bcd' , `age` = null , `birthday` = null , `create_time` = null , `update_time` = null
         * WHERE `id` = 1
         */
        size = accountMapper.update(account2, false);
        Assertions.assertEquals(1, size);

        // 查询数据
        Account accountDb = accountMapper.selectOneById(account.getId());
        Assertions.assertNotNull(accountDb);
        Assertions.assertNull(accountDb.getAge());
    }
}

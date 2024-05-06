package com.github.junit;

import static com.github.domain.table.AccountTableDef.ACCOUNT;

import com.github.Application;
import com.github.domain.Account;
import com.github.mapper.AccountMapper;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
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
class DbRowTest {
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

        // 准备数据
        List<Account> accountList = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Account account = new Account();
            account.setUserName("userName" + i);
            account.setAge(i);
            account.setGender(i % 2);
            accountList.add(account);
        }

        accountMapper.insertBatch(accountList);
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
    void testInsertRow() {
        RowKey rowKey = RowKey.of("id", KeyType.Auto);

        // 使用 Row 插入数据
        Row row = Row.ofKey(rowKey);
        row.set(ACCOUNT.USER_NAME, "无名");
        row.set(ACCOUNT.AGE, 1);

        int size = Db.insert("tb_account", row);

        log.info("DbRowTest.testInsertRow.id ==> {}", row.getInt("id"));

        Assertions.assertEquals(1, size);
    }

    @Test
    void testInsertSQL() {
        String sql = """
            insert into tb_account(user_name) value (?)
        """;

        int size = Db.insertBySql(sql, "呵呵哒");

        Assertions.assertEquals(1, size);
    }

    @Test
    void testSelect() {
        String sql = """
            SELECT * FROM tb_account where id > ?
        """;
        List<Row> rows = Db.selectListBySql(sql, 1);
        Assertions.assertFalse(rows.isEmpty());
    }

    @Test
    void testSelectByQW() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select()
                .from(ACCOUNT)
                .where(ACCOUNT.ID.gt(1))
                .limit(1);

        Row row = Db.selectOneByQuery(queryWrapper);

        log.info("DbRowTest.testSelectByQW.id ==> {}", row.getInt("id"));

        Assertions.assertNotNull(row);
    }
}

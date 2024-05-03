package com.github.mysql.junit;

import com.github.Application;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class FieldQueryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
    private StopWatch stopWatch;
    private TestInfo currentTestInfo;

    private static void printSQL(QueryWrapper queryWrapper) {
        String sql = queryWrapper.toSQL();
        log.info(SqlFormatter.format(sql));
    }

    private static boolean compareSQL(String sql1, String sql2) {
        try {
            Statement stmt1 = CCJSqlParserUtil.parse(sql1);
            Statement stmt2 = CCJSqlParserUtil.parse(sql2);
            return stmt1
                    .toString()
                    .equals(stmt2.toString());
        } catch (Exception e) {
            log.info("JoinQueryTest.compareSQL ==> {}", e.getMessage());
            return false;
        }
    }

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
                Objects
                        .requireNonNull(currentTestInfo
                                .getTestMethod()
                                .orElse(null))
                        .getName(),
                stopWatch.getTotalTimeMillis());
    }

    @Test
    void testFieldQuery() {
        
    }
}

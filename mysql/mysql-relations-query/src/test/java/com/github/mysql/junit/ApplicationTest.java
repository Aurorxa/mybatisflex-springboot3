package com.github.mysql.junit;

import com.github.Application;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

import static com.github.domain.extra.table.AccountRoleTableDef.ACCOUNT_ROLE;
import static com.github.domain.extra.table.OrderGoodTableDef.ORDER_GOOD;
import static com.github.domain.table.AccountTableDef.ACCOUNT;
import static com.github.domain.table.GoodTableDef.GOOD;
import static com.github.domain.table.OrderTableDef.ORDER;
import static com.github.domain.table.RoleTableDef.ROLE;

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
            log.info("QuickStartTest.compareSQL ==> {}", e.getMessage());
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
    void test() {

        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select(ACCOUNT.DEFAULT_COLUMNS, ROLE.DEFAULT_COLUMNS, ORDER.DEFAULT_COLUMNS, GOOD.DEFAULT_COLUMNS)
                .from(ACCOUNT)
                .leftJoin(ACCOUNT_ROLE)
                .on(ACCOUNT.ID.eq(ACCOUNT_ROLE.ACCOUNT_ID))
                .leftJoin(ROLE)
                .on(ACCOUNT_ROLE.ROLE_ID.eq(ROLE.ID))
                .leftJoin(ORDER)
                .on(ORDER.ACCOUNT_ID.eq(ACCOUNT.ID))
                .leftJoin(ORDER_GOOD)
                .on(ORDER.ID.eq(ORDER_GOOD.ORDER_ID))
                .leftJoin(GOOD)
                .on(ORDER_GOOD.GOOD_ID.eq(GOOD.ID))
                .where(ACCOUNT.ID.lt(10));

        String sql =
                """
                        SELECT
                        	`tb_account`.`id` AS `tb_account$id`,
                        	`tb_account`.`age`,
                        	`tb_account`.`grade`,
                        	`tb_account`.`money`,
                        	`tb_account`.`birthday`,
                        	`tb_account`.`user_name`,
                        	`tb_account`.`create_time` AS `tb_account$create_time`,
                        	`tb_account`.`update_time` AS `tb_account$update_time`,
                        	`tb_role`.`id` AS `tb_role$id`,
                        	`tb_role`.`role_name`,
                        	`tb_role`.`create_time` AS `tb_role$create_time`,
                        	`tb_role`.`update_time` AS `tb_role$update_time`,
                        	`tb_order`.`id` AS `tb_order$id`,
                        	`tb_order`.`order_key`,
                        	`tb_order`.`account_id` AS `tb_order$account_id`,
                        	`tb_order`.`order_name`,
                        	`tb_order`.`create_time` AS `tb_order$create_time`,
                        	`tb_order`.`update_time` AS `tb_order$update_time`,
                        	`tb_good`.`id` AS `tb_good$id`,
                        	`tb_good`.`good_name`,
                        	`tb_good`.`account_id` AS `tb_good$account_id`,
                        	`tb_good`.`create_time` AS `tb_good$create_time`,
                        	`tb_good`.`update_time` AS `tb_good$update_time`
                        FROM
                        	`tb_account`
                        	LEFT JOIN `tb_account_role` ON `tb_account`.`id` = `tb_account_role`.`account_id`
                        	LEFT JOIN `tb_role` ON `tb_account_role`.`role_id` = `tb_role`.`id`
                        	LEFT JOIN `tb_order` ON `tb_order`.`account_id` = `tb_account`.`id`
                        	LEFT JOIN `tb_order_good` ON `tb_order`.`id` = `tb_order_good`.`order_id`
                        	LEFT JOIN `tb_good` ON `tb_order_good`.`good_id` = `tb_good`.`id`
                        WHERE
                        	`tb_account`.`id` < 10
                        """;

        log.info("ApplicationTest.test {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }
}

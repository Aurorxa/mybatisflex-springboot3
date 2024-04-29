package com.github.querywrapper;

import static com.github.domain.table.AccountTableDef.ACCOUNT;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.mybatisflex.core.query.If;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class DynamicSQLTest {

    private static void printSQL(QueryWrapper queryWrapper) {
        String sql = queryWrapper.toSQL();
        log.info(SqlFormatter.format(sql));
    }

    private static boolean compareSQL(String sql1, String sql2) {
        try {
            Statement stmt1 = CCJSqlParserUtil.parse(sql1);
            Statement stmt2 = CCJSqlParserUtil.parse(sql2);
            return stmt1.toString().equals(stmt2.toString());
        } catch (Exception e) {
            log.info("DynamicSQLTest.compareSQL ==> {}", e.getMessage());
            return false;
        }
    }

    @Test
    @SuppressWarnings("all")
    void testDynamicSQL() {
        QueryWrapper queryWrapper = QueryWrapper.create().select().from(ACCOUNT);

        // 可能为 Null 或 ""
        String userName = "张三";

        // 如果字符串为空，则为 true；否则，为 false
        boolean flag = null != userName && !userName.trim().isEmpty();

        queryWrapper.where(flag ? ACCOUNT.USER_NAME.eq(userName) : QueryMethods.noCondition());

        String sql =
                """
                SELECT
                	*
                FROM
                	`tb_account`
                WHERE
                	`user_name` = '张三'
                """;

        log.info("DynamicSQLTest.testDynamicSQL {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    @SuppressWarnings("all")
    void testDynamicSQL2() {
        QueryWrapper queryWrapper = QueryWrapper.create().select().from(ACCOUNT);

        // 可能为 Null 或 ""
        String userName = "张三";

        // 如果字符串为空，则为 true；否则，为 false
        boolean flag = null != userName && !userName.trim().isEmpty();

        queryWrapper.where(ACCOUNT.USER_NAME.eq(userName).when(flag));

        String sql =
                """
                SELECT
                	*
                FROM
                	`tb_account`
                WHERE
                	`user_name` = '张三'
                """;

        log.info("DynamicSQLTest.testDynamicSQL2 {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    void testDynamicSQL3() {
        QueryWrapper queryWrapper = QueryWrapper.create().select().from(ACCOUNT);

        // 可能为 Null 或 ""
        String userName = "张三";

        queryWrapper.where(ACCOUNT.USER_NAME.eq(userName).when(() -> If.hasText(userName)));

        String sql =
                """
                SELECT
                	*
                FROM
                	`tb_account`
                WHERE
                	`user_name` = '张三'
                """;

        log.info("DynamicSQLTest.testDynamicSQL3 {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    void testDynamicSQL4() {
        QueryWrapper queryWrapper = QueryWrapper.create().select().from(ACCOUNT);

        // 可能为 Null 或 ""
        String userName = "";

        queryWrapper.where(ACCOUNT.USER_NAME.eq(userName, If::hasText));

        String sql =
                """
                SELECT
                	*
                FROM
                	`tb_account`
                """;

        log.info("DynamicSQLTest.testDynamicSQL4 {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    void testDynamicSQL5() {
        QueryWrapper queryWrapper = QueryWrapper.create().select().from(ACCOUNT);

        // 可能为 Null 或 ""
        String userName = "";

        // 年龄
        Integer age = 18;

        // 余额
        BigDecimal money = new BigDecimal(100);

        queryWrapper.where(ACCOUNT.USER_NAME.eq(userName, If::hasText));
        queryWrapper.where(ACCOUNT.AGE.eq(age, If::notNull));
        queryWrapper.where(ACCOUNT.MONEY.eq(money, If::notNull));

        String sql =
                """
               SELECT
                   *
               FROM
                   `tb_account`
               WHERE
                   `money` = 100
                   AND `age` = 18
               """;

        log.info("DynamicSQLTest.testDynamicSQL5 {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }
}

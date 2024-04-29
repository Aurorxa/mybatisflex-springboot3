package com.github.querywrapper;

import static com.github.domain.table.AccountTableDef.ACCOUNT;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class SQLFunctionTest {

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
            log.info("SQLFunctionTest ==> {}", e.getMessage());
            return false;
        }
    }

    @Test
    void testFunction() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(QueryMethods.concat(ACCOUNT.USER_NAME, QueryMethods.string("123")))
                .from(ACCOUNT)
                .where(QueryMethods.not(ACCOUNT.GENDER.eq(3)));

        String sql =
                """
                SELECT
                	CONCAT( `user_name`, '123' )
                FROM
                	`tb_account`
                WHERE
                	NOT (
                	`gender` = 3)
                """;

        log.info("SQLFunctionTest.testFunction {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    void testFunction2() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(QueryMethods.avg(ACCOUNT.AGE), QueryMethods.max(ACCOUNT.AGE))
                .from(ACCOUNT);

        String sql =
                """
               SELECT AVG(`age`), MAX(`age`)
               FROM `tb_account`
               """;

        log.info("SQLFunctionTest.testFunction2 {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    void testFunction3() {
        QueryWrapper queryWrapper =
                QueryWrapper.create().select(ACCOUNT.ID, ACCOUNT.AGE.add(10)).from(ACCOUNT);

        String sql = """
               SELECT `id`, `age` + 10
               FROM `tb_account`
               """;

        log.info("SQLFunctionTest.testFunction3 {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }
}

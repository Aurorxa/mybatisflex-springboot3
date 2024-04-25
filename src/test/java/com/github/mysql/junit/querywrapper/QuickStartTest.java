package com.github.mysql.junit.querywrapper;

import com.github.domain.Account;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.mybatisflex.core.query.If;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.domain.table.AccountRoleTableDef.ACCOUNT_ROLE;
import static com.github.domain.table.AccountTableDef.ACCOUNT;
import static com.github.domain.table.RoleTableDef.ROLE;

@Slf4j
class QuickStartTest {

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
            log.info("compareSQL ==> {}", e.getMessage());
            return false;
        }
    }


    @Test
    void testSimple() {
        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select(ACCOUNT.ALL_COLUMNS, ROLE.ALL_COLUMNS)
                .from(ACCOUNT)
                .leftJoin(ACCOUNT_ROLE)
                .on(ACCOUNT.ID.eq(ACCOUNT_ROLE.ACCOUNT_ID))
                .leftJoin(ROLE)
                .on(ACCOUNT_ROLE.ROLE_ID.eq(ROLE.ID))
                .where(ACCOUNT.ID.ge(1))
                .groupBy(ACCOUNT.AGE)
                .having(ACCOUNT.GENDER.in(1, 2))
                .orderBy(ACCOUNT.ID.desc())
                .union(QueryWrapper
                        .create()
                        .select(ACCOUNT.ALL_COLUMNS)
                        .from(ACCOUNT))
                .limit(10);

        String sql = """
                ( SELECT
                `tb_account`.*,
                `tb_role`.*
                FROM
                	`tb_account`
                	LEFT JOIN `tb_account_role` ON `tb_account`.`id` = `tb_account_role`.`account_id`
                	LEFT JOIN `tb_role` ON `tb_account_role`.`role_id` = `tb_role`.`id`
                WHERE
                	`tb_account`.`id` >= 1
                GROUP BY
                	`tb_account`.`age`
                HAVING
                	`tb_account`.`gender` IN ( 1, 2 )
                ORDER BY
                	`tb_account`.`id` DESC
                ) UNION
                ( SELECT * FROM `tb_account` )
                LIMIT 10""";

        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    @SuppressWarnings("all")
    void testDynamicSQL() {
        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select()
                .from(ACCOUNT);

        // 可能为 Null 或 ""
        String userName = "张三";

        // 如果字符串为空，则为 true；否则，为 false
        boolean flag = null != userName && !userName
                .trim()
                .isEmpty();

        queryWrapper.where(flag ? ACCOUNT.USER_NAME.eq(userName) : QueryMethods.noCondition());

        String sql = """
                SELECT
                	*
                FROM
                	`tb_account`
                WHERE
                	`user_name` = '张三'
                """;

        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);

    }

    @Test
    @SuppressWarnings("all")
    void testDynamicSQL2() {
        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select()
                .from(ACCOUNT);

        // 可能为 Null 或 ""
        String userName = "张三";

        // 如果字符串为空，则为 true；否则，为 false
        boolean flag = null != userName && !userName
                .trim()
                .isEmpty();

        queryWrapper.where(ACCOUNT.USER_NAME
                .eq(userName)
                .when(flag));

        String sql = """
                SELECT
                	*
                FROM
                	`tb_account`
                WHERE
                	`user_name` = '张三'
                """;

        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);

    }

    @Test
    void testDynamicSQL3() {
        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select()
                .from(ACCOUNT);

        // 可能为 Null 或 ""
        String userName = "张三";

        queryWrapper.where(ACCOUNT.USER_NAME
                .eq(userName)
                .when(() -> If.hasText(userName)));

        String sql = """
                SELECT
                	*
                FROM
                	`tb_account`
                WHERE
                	`user_name` = '张三'
                """;

        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    void testDynamicSQL4() {
        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select()
                .from(ACCOUNT);

        // 可能为 Null 或 ""
        String userName = "";

        queryWrapper.where(ACCOUNT.USER_NAME
                .eq(userName, If::hasText)
                .when(() -> If.hasText(userName)));

        String sql = """
                SELECT
                	*
                FROM
                	`tb_account`
                """;

        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    void testFunction() {
        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select(QueryMethods.concat(ACCOUNT.USER_NAME, QueryMethods.string("123")))
                .from(ACCOUNT)
                .where(QueryMethods.not(ACCOUNT.GENDER.eq(3)));

        String sql = """
                SELECT
                	CONCAT( `user_name`, '123' )
                FROM
                	`tb_account`
                WHERE
                	NOT (
                	`gender` = 3)
                """;

        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    @SuppressWarnings("all")
    void testLambda() {
        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select(Account::getId, Account::getUserName, Account::getAge)
                .from(Account.class)
                .where(Account::getId)
                .eq(1);
        String sql = """
                SELECT `id`, `user_name`, `age` 
                FROM `tb_account` 
                WHERE `id` = 1
                """;

        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }


}

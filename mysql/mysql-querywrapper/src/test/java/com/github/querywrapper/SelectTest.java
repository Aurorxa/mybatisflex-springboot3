package com.github.querywrapper;

import static com.github.domain.table.AccountRoleTableDef.ACCOUNT_ROLE;
import static com.github.domain.table.AccountTableDef.ACCOUNT;
import static com.github.domain.table.RoleTableDef.ROLE;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
class SelectTest {

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
            log.info("SelectTest.compareSQL ==> {}", e.getMessage());
            return false;
        }
    }

    @Test
    void testSelectAll() {
        QueryWrapper queryWrapper = QueryWrapper.create().select().from(ACCOUNT);

        String sql =
                """
                SELECT
                	*
                FROM
                	`tb_account`
                """;
        log.info("SelectTest.testSelectAll {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    void testSelectColumn() {
        QueryWrapper queryWrapper =
                QueryWrapper.create().select(ACCOUNT.ID, ACCOUNT.AGE).from(ACCOUNT);

        String sql =
                """
                SELECT
                	`id`,`age`
                FROM
                	`tb_account`
                """;
        log.info("SelectTest.testSelectColumn {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    void testSelectColumnAs() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(ACCOUNT.ID.as("accountId"), ACCOUNT.AGE)
                .from(ACCOUNT);

        String sql =
                """
                SELECT
                	`id` AS `accountId`,`age`
                FROM
                	`tb_account`
                """;

        log.info("SelectTest.testSelectColumnAs {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    void testSelectMultipleTable() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(ACCOUNT.ID, ACCOUNT.AGE)
                .from(ACCOUNT.as("a"))
                .leftJoin(ACCOUNT_ROLE.as("ar"))
                .on(ACCOUNT.ID.eq(ACCOUNT_ROLE.ACCOUNT_ID))
                .leftJoin(ROLE.as("r"))
                .on(ACCOUNT_ROLE.ROLE_ID.eq(ROLE.ID));

        String sql =
                """
                SELECT
                    `a`.`id`,
                    `a`.`age`
                FROM
                    `tb_account` AS `a`
                    LEFT JOIN `tb_account_role` AS `ar` ON `a`.`id` = `ar`.`account_id`
                    LEFT JOIN `tb_role` AS `r` ON `ar`.`role_id` = `r`.`id`
                """;

        log.info("SelectTest.testSelectMultipleTable {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }
}

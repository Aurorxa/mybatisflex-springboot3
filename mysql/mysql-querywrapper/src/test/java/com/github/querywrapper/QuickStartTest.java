package com.github.querywrapper;

import static com.github.domain.table.AccountTableDef.*;
import static com.github.domain.table.RoleTableDef.*;

import com.github.domain.Account;
import com.github.domain.table.AccountRoleTableDef;
import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
            return stmt1.toString().equals(stmt2.toString());
        } catch (Exception e) {
            log.info("QuickStartTest.compareSQL ==> {}", e.getMessage());
            return false;
        }
    }

    @Test
    void testSimple() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(ACCOUNT.DEFAULT_COLUMNS, ROLE.DEFAULT_COLUMNS)
                .from(ACCOUNT)
                .leftJoin(AccountRoleTableDef.ACCOUNT_ROLE)
                .on(ACCOUNT.ID.eq(AccountRoleTableDef.ACCOUNT_ROLE.ACCOUNT_ID))
                .leftJoin(ROLE)
                .on(AccountRoleTableDef.ACCOUNT_ROLE.ROLE_ID.eq(ROLE.ID))
                .where(ACCOUNT.ID.ge(1))
                .groupBy(ACCOUNT.AGE)
                .having(ACCOUNT.GENDER.in(1, 2))
                .orderBy(ACCOUNT.ID.desc())
                .union(QueryWrapper.create().select(ACCOUNT.ALL_COLUMNS).from(ACCOUNT))
                .limit(10);

        String sql =
                """
                (SELECT `tb_account`.`id` AS `tb_account$id`, `tb_account`.`age`, `tb_account`.`money`, `tb_account`.`gender`, `tb_account`.`birthday`, `tb_account`.`user_name`, `tb_account`.`create_time` AS `tb_account$create_time`, `tb_account`.`update_time` AS `tb_account$update_time`, `tb_role`.`id` AS `tb_role$id`, `tb_role`.`role_name`, `tb_role`.`create_time` AS `tb_role$create_time`, `tb_role`.`update_time` AS `tb_role$update_time`
                FROM `tb_account`
                LEFT JOIN `tb_account_role`
                ON `tb_account`.`id` = `tb_account_role`.`account_id`
                LEFT JOIN `tb_role`
                ON `tb_account_role`.`role_id` = `tb_role`.`id`
                WHERE `tb_account`.`id` >= 1
                GROUP BY `tb_account`.`age`
                HAVING `tb_account`.`gender` IN (1, 2)
                ORDER BY `tb_account`.`id` DESC)
                UNION
                (SELECT * FROM `tb_account`)
                LIMIT 10""";

        log.info("QuickStartTest.testSimple {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }

    @Test
    @SuppressWarnings("all")
    void testLambda() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(Account::getId, Account::getUserName, Account::getAge)
                .from(Account.class)
                .where(Account::getId)
                .eq(1);
        String sql =
                """
                SELECT `id`, `user_name`, `age`
                FROM `tb_account`
                WHERE `id` = 1
                """;

        log.info("QuickStartTest.testLambda {}", queryWrapper.toSQL());
        Assertions.assertTrue(compareSQL(queryWrapper.toSQL(), sql));

        printSQL(queryWrapper);
    }
}

package com.github.mysql.junit.querywrapper;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.mybatisflex.core.query.If;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static com.github.domain.table.AccountRoleTableDef.ACCOUNT_ROLE;
import static com.github.domain.table.AccountTableDef.ACCOUNT;
import static com.github.domain.table.RoleTableDef.ROLE;

@Slf4j
@SuppressWarnings("all")
class QuickStartTest {

    private static void printSQL(QueryWrapper queryWrapper) {
        String sql = queryWrapper.toSQL();
        log.info(SqlFormatter.format(sql));
    }

    @Test
    void testSimple() {
        /*
         ( SELECT
         ` tb_account `.*,
         ` tb_role `.*
         FROM
         ` tb_account `
         LEFT JOIN ` tb_account_role ` ON ` tb_account `.` id `  = ` tb_account_role `.` account_id `
         LEFT JOIN ` tb_role ` ON ` tb_account_role `.` role_id ` = ` tb_role `.` id `
         WHERE ` tb_account `.` id ` >= 1
         GROUP BY ` tb_account `.` age `
         HAVING ` tb_account `.` gender ` IN (1, 2)
         ORDER BY ` tb_account `.` id ` DESC )
         UNION
         ( SELECT *
         FROM ` tb_account `)
         LIMIT 10
         */
        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select(ACCOUNT.ALL_COLUMNS, ROLE.ALL_COLUMNS)
                .from(ACCOUNT)
                .leftJoin(ACCOUNT_ROLE)
                .on((ACCOUNT.ID.eq(ACCOUNT_ROLE.ACCOUNT_ID)))
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

        printSQL(queryWrapper);

    }

    @Test
    void testDynamic() {
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

        printSQL(queryWrapper);

    }

    @Test
    void testDynamic2() {
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

        printSQL(queryWrapper);

    }

    @Test
    void testDynamic3() {
        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select()
                .from(ACCOUNT);

        // 可能为 Null 或 ""
        String userName = "张三";

        queryWrapper.where(ACCOUNT.USER_NAME
                .eq(userName)
                .when(() -> If.hasText(userName)));

        printSQL(queryWrapper);
    }

    @Test
    void testDynamic4() {
        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select()
                .from(ACCOUNT);

        // 可能为 Null 或 ""
        String userName = "";

        queryWrapper.where(ACCOUNT.USER_NAME
                .eq(userName)
                .when(() -> If.hasText(userName)));

        printSQL(queryWrapper);
    }

    @Test
    void testDynamic5() {
        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select()
                .from(ACCOUNT);

        // 可能为 Null 或 ""
        String userName = "";

        queryWrapper.where(ACCOUNT.USER_NAME
                .eq(userName, If::hasText)
                .when(() -> If.hasText(userName)));

        printSQL(queryWrapper);
    }


}

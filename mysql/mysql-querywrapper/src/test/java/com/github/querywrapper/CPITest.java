package com.github.querywrapper;

import com.github.domain.Account;
import com.mybatisflex.core.query.CPI;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryWrapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class CPITest {

    @SuppressWarnings("all")
    @Test
    void testCPI() {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(Account::getId, Account::getUserName, Account::getAge)
                .from(Account.class)
                .where(Account::getId)
                .eq(1)
                .limit(10);
        List<QueryColumn> selectColumns = CPI.getSelectColumns(queryWrapper);
        log.info("SelectTest.testCPI.selectColumns ==> {}", selectColumns);
        CPI.setLimitRows(queryWrapper, 1L);
        log.info("SelectTest.testCPI.queryWrapper ==> {}", queryWrapper.toSQL());
    }
}

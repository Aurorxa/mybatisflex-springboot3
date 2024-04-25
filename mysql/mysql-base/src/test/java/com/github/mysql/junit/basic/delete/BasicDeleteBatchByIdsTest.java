package com.github.mysql.junit.basic.delete;

import com.github.Application;
import com.github.domain.Account;
import com.github.domain.table.AccountTableDef;
import com.github.mapper.AccountMapper;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class BasicDeleteBatchByIdsTest {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));
    @Resource
    private AccountMapper accountMapper;

    @Test
    void testDeleteBatchByIds() {
        // 新增数据
        List<Account> accountList = new ArrayList<>();
        accountList.add(new Account().setUserName("a"));
        accountList.add(new Account().setUserName("b"));
        accountList.add(new Account().setUserName("c"));
        accountList.add(new Account().setUserName("d"));

        accountMapper.insertBatch(accountList);

        // 查询数据
        List<Account> accountDbList = accountMapper.selectAll();
        Assertions.assertEquals(4, accountDbList.size());
        Assertions.assertTrue(accountDbList
                .stream()
                .map(Account::getUserName)
                .toList()
                .contains("a"));

        /*
          删除数据

          DELETE FROM `tb_account`
          WHERE `id` = ? OR `id` = ? OR `id` = ? OR `id` = ?
         */
        List<Long> idsArray = accountDbList
                .stream()
                .map(Account::getId)
                .toList();
        int size = accountMapper.deleteBatchByIds(idsArray);
        Assertions.assertEquals(4, size);

        // 查询数据
        QueryWrapper queryWrapper = QueryWrapper
                .create()
                .select(AccountTableDef.ACCOUNT.ALL_COLUMNS)
                .from(AccountTableDef.ACCOUNT)
                .where(AccountTableDef.ACCOUNT.USER_NAME.in(List.of("a", "b", "c", "d")));
        accountDbList = accountMapper.selectListByQuery(queryWrapper);
        Assertions.assertEquals(0, accountDbList.size());
    }

}

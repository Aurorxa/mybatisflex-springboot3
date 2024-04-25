package com.github.mysql.junit;

import com.github.Application;
import com.github.domain.Account;
import com.github.domain.Role;
import com.github.mapper.AccountMapper;
import com.github.mapper.RoleMapper;
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

import java.util.List;

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class ApplicationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private RoleMapper roleMapper;

    @Test
    void testNoGenericsTypeInsert() {
        // 封装数据
        Account account = new Account();
        account.setUserName("张三");

        // 新增数据
        accountMapper.insert(account);

        // 查询数据
        Account accountDb = accountMapper.selectOneById(account.getId());

        // 断言
        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getUserName());
        Assertions.assertNotNull(accountDb.getCreateTime());
        Assertions.assertNotNull(accountDb.getBirthday());
    }

    @Test
    void testNoGenericsTypeUpdate() {
        // 封装数据
        Account account = new Account();
        account.setUserName("张三");

        // 新增数据
        accountMapper.insert(account);

        // 更新数据
        account.setUserName("李四");
        accountMapper.update(account);

        // 查询数据
        Account accountDb = accountMapper.selectOneById(account.getId());

        // 断言
        Assertions.assertNotNull(accountDb);
        Assertions.assertNotNull(accountDb.getGender());
        Assertions.assertNotNull(accountDb.getCreateTime());
        Assertions.assertNotNull(accountDb.getBirthday());
        Assertions.assertNotNull(accountDb.getUpdateTime());
    }

    @Test
    void testGenericsTypeInsert() {
        // 封装数据
        Role role = new Role();
        role.setRoleName("测试角色");

        // 新增数据
        roleMapper.insert(role);

        // 查询数据
        Role roleDb = roleMapper.selectOneById(role.getId());

        // 断言
        Assertions.assertNotNull(roleDb);
        Assertions.assertNotNull(roleDb.getRoleName());
        Assertions.assertNotNull(roleDb.getCreateTime());
    }


    @Test
    void testGenericsTypeUpdate() {
        // 封装数据
        Role role = new Role();
        role.setRoleName("测试角色");

        // 新增数据
        roleMapper.insert(role);

        // 更新数据
        role.setRoleName("测试角色2");
        roleMapper.update(role);

        // 查询数据
        Role roleDb = roleMapper.selectOneById(role.getId());

        // 断言
        Assertions.assertNotNull(roleDb);
        Assertions.assertNotNull(roleDb.getRoleName());
        Assertions.assertNotNull(roleDb.getCreateTime());
        Assertions.assertNotNull(roleDb.getUpdateTime());
    }

    @Test
    void testSetListener() {
        // 封装数据
        Account account = new Account();
        account.setUserName("admin");
        account.setPassword("123456");

        // 新增数据
        accountMapper.insert(account);

        // 封装数据
        Account account2 = new Account();
        account2.setUserName("zhangsan");
        account2.setPassword("123456");

        // 新增数据
        accountMapper.insert(account2);

        // 封装数据
        Account account3 = new Account();
        account3.setUserName("lisi");
        account3.setPassword("123456");

        // 新增数据
        accountMapper.insert(account3);

        // 查询数据
        List<Account> accountList = accountMapper.selectAll();
        log.info("testSetListener.accountList ==> {}", accountList);

        // 断言
        Assertions.assertNotNull(accountList);

    }

}

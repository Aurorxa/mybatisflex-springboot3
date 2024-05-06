package com.github.mysql.junit;

import com.github.Application;
import com.github.domain.Account;
import com.github.domain.Good;
import com.github.domain.Order;
import com.github.domain.Role;
import com.github.domain.extra.AccountRole;
import com.github.domain.extra.OrderGood;
import com.github.mapper.*;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.relation.RelationManager;
import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class RelationQueryTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));

    private StopWatch stopWatch;
    private TestInfo currentTestInfo;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private AccountRoleMapper accountRoleMapper;

    @Resource
    private GoodMapper goodMapper;

    @Resource
    private OrderGoodMapper orderGoodMapper;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        stopWatch = new StopWatch();
        stopWatch.start();
        currentTestInfo = testInfo;

        // 准备数据

        // === 用户数据 ===
        Account adminAccount = new Account()
                .setUserName("admin")
                .setMoney(new BigDecimal("1000.00"))
                .setGrade(1)
                .setBirthday(new Date())
                .setAge(20);
        Account zhangsanAccount = new Account()
                .setUserName("zhangsan")
                .setMoney(new BigDecimal("100.00"))
                .setGrade(2)
                .setBirthday(new Date())
                .setAge(18);

        int size = accountMapper.insertSelective(adminAccount);
        log.info("JoinQueryTest.test.adminAccount ==> {}", size);

        size = accountMapper.insertSelective(zhangsanAccount);
        log.info("JoinQueryTest.test.zhangsanAccount ==> {}", size);

        // === 角色数据 ===
        Role adminRole = new Role().setRoleName("管理员");
        Role zhangsanRole = new Role().setRoleName("普通用户");

        size = roleMapper.insertSelective(adminRole);
        log.info("JoinQueryTest.test.adminRole ==> {}", size);

        size = roleMapper.insertSelective(zhangsanRole);
        log.info("JoinQueryTest.test.zhangsanRole ==> {}", size);

        // === 用户和角色数据 ===
        AccountRole adminAccountRole =
                new AccountRole().setAccountId(adminAccount.getId()).setRoleId(adminRole.getId());
        AccountRole zhangsanAccountRole =
                new AccountRole().setAccountId(zhangsanAccount.getId()).setRoleId(zhangsanRole.getId());

        size = accountRoleMapper.insertSelective(adminAccountRole);
        log.info("JoinQueryTest.test.adminAccountRole ==> {}", size);

        size = accountRoleMapper.insertSelective(zhangsanAccountRole);
        log.info("JoinQueryTest.test.zhangsanAccountRole ==> {}", size);

        // === 订单数据 ===
        Order adminOrder = new Order().setOrderKey("1").setOrderName("订单1").setAccountId(adminAccount.getId());
        Order adminOrder2 = new Order().setOrderKey("2").setOrderName("订单2").setAccountId(adminAccount.getId());
        Order zhangsanOrder = new Order().setOrderKey("3").setOrderName("订单3").setAccountId(zhangsanAccount.getId());

        size = orderMapper.insertSelective(adminOrder);
        log.info("JoinQueryTest.test.adminOrder ==> {}", size);

        size = orderMapper.insertSelective(adminOrder2);
        log.info("JoinQueryTest.test.adminOrder2 ==> {}", size);

        size = orderMapper.insertSelective(zhangsanOrder);
        log.info("JoinQueryTest.test.zhangsanOrder ==> {}", size);

        // === 商品数据 ===
        Good good1 = new Good().setGoodName("商品1");
        Good good2 = new Good().setGoodName("商品2");
        Good good3 = new Good().setGoodName("商品3");

        size = goodMapper.insertSelective(good1);
        log.info("JoinQueryTest.test.good1 ==> {}", size);

        size = goodMapper.insertSelective(good2);
        log.info("JoinQueryTest.test.good2 ==> {}", size);

        size = goodMapper.insertSelective(good3);
        log.info("JoinQueryTest.test.good3 ==> {}", size);

        // === 用户和商品数据 ===
        OrderGood orderGood1 = new OrderGood().setOrderId(adminOrder.getId()).setGoodId(good1.getId());
        OrderGood orderGood2 = new OrderGood().setOrderId(adminOrder.getId()).setGoodId(good2.getId());
        OrderGood orderGood3 = new OrderGood().setOrderId(adminOrder.getId()).setGoodId(good3.getId());
        OrderGood orderGood6 = new OrderGood().setOrderId(adminOrder2.getId()).setGoodId(good3.getId());
        OrderGood orderGood7 = new OrderGood().setOrderId(zhangsanOrder.getId()).setGoodId(good1.getId());
        OrderGood orderGood8 = new OrderGood().setOrderId(zhangsanOrder.getId()).setGoodId(good2.getId());

        size = orderGoodMapper.insertSelective(orderGood1);
        log.info("JoinQueryTest.test.orderGood1 ==> {}", size);
        size = orderGoodMapper.insertSelective(orderGood2);
        log.info("JoinQueryTest.test.orderGood2 ==> {}", size);
        size = orderGoodMapper.insertSelective(orderGood3);
        log.info("JoinQueryTest.test.orderGood3 ==> {}", size);
        size = orderGoodMapper.insertSelective(orderGood6);
        log.info("JoinQueryTest.test.orderGood6 ==> {}", size);
        size = orderGoodMapper.insertSelective(orderGood7);
        log.info("JoinQueryTest.test.orderGood1 ==> {}", size);
        size = orderGoodMapper.insertSelective(orderGood8);
        log.info("JoinQueryTest.test.orderGood8 ==> {}", size);
    }

    @AfterEach
    public void tearDown() {
        stopWatch.stop();
        log.info(
                "Test 方法：{}  execution time: {} ms ",
                Objects.requireNonNull(currentTestInfo.getTestMethod().orElse(null))
                        .getName(),
                stopWatch.getTotalTimeMillis());
    }

    @Test
    void test() {

        RelationManager.addIgnoreRelations("orderList");

        QueryWrapper queryWrapper = QueryWrapper.create();

        List<Account> accountList = accountMapper.selectListWithRelationsByQuery(queryWrapper);

        log.info("accountList ==> {}", accountList);

        Assertions.assertNotNull(accountList);
    }
}

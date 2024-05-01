package com.github.mysql.junit;

import com.github.tenant.Application;
import com.github.tenant.domain.Account;
import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class ApplicationTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));

    @LocalServerPort
    private Integer port;

    private StopWatch stopWatch;

    private TestInfo currentTestInfo;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        RestAssured.baseURI = "http://localhost:" + port;
        stopWatch = new StopWatch();
        stopWatch.start();
        currentTestInfo = testInfo;
    }

    @AfterEach
    public void tearDown() {
        stopWatch.stop();
        log.info(
                "Test 方法：{}  execution time: {} ms ",
                Objects
                        .requireNonNull(currentTestInfo
                                .getTestMethod()
                                .orElse(null))
                        .getName(),
                stopWatch.getTotalTimeMillis());
    }

    @Test
    void testWeb() {

        /*
         * 注册，即初始化流程
         */
        Account account = new Account();
        account.setUserName("zhangsan");
        // 假设后台自动生成
        account.setTenantId(1L);

        given()
                .contentType("application/json")
                .body(account)
                .log()
                .all()  // 这将打印出整个请求的详细信息
                .when()
                .post("/account/add")
                .then()
                .log()
                .all()  // 这将打印出响应的详细信息
                .statusCode(200);  // 检查状态码，确保数据已成功添加



        /*
         * 查询流程
         */
        given()
                .headers("tenantId", account.getTenantId())
                .log()
                .all()  // 这将打印出整个请求的详细信息
                .when()
                .get("/account/list")
                .then()
                .log()
                .all()  // 这将打印出响应的详细信息
                .statusCode(200)
                .body("", hasSize(1));  // 检查返回的列表是否非空
    }


}

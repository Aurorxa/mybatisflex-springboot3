package com.github.mysql.junit.web;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

import com.github.Application;
import com.github.dao.CustomerRepository;
import com.github.domain.Customer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Objects;
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

@Slf4j
@Transactional
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
class WebAPITest {
    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8"));

    @LocalServerPort
    private Integer port;

    @Resource
    private CustomerRepository customerRepository;

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
                Objects.requireNonNull(currentTestInfo.getTestMethod().orElse(null))
                        .getName(),
                stopWatch.getTotalTimeMillis());
    }

    @Test
    void test() {

        Customer customer = new Customer();
        customer.setEmail("123456789@qq.com");
        customer.setName("你大爷的");

        Customer customerDb = customerRepository.save(customer);
        log.info("ApplicationTest.test.customerDb ==> {}", customerDb);

        List<Customer> all = customerRepository.findAll();
        log.info("ApplicationTest.test.all ==> {}", all);

        given().contentType(ContentType.JSON)
                .when()
                .get("/api/customers")
                .then()
                .statusCode(200)
                .body(".", hasSize(1));
    }
}

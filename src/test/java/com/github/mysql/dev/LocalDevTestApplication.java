package com.github.mysql.dev;

import com.github.Application;
import com.github.dao.CustomerRepository;
import com.github.domain.Customer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Slf4j
@Testcontainers
public class LocalDevTestApplication {

    @Resource
    private CustomerRepository customerRepository;

    public static void main(String[] args) {
        SpringApplication
                .from(Application::main)
                .with(LocalDevTestContainersConfiguration.class)
                .run(args);
    }

    public void test() {
        List<Customer> all = customerRepository.findAll();
        log.info("LocalDevTestApplication.test ==> {}", all);
    }

}

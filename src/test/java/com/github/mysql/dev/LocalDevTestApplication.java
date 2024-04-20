package com.github.mysql.dev;

import com.github.Application;
import com.github.dao.CustomerRepository;
import com.github.domain.Customer;
import jakarta.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

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
        List<Customer> customers = List.of(
                new Customer(null, "John", "john@mail.com"),
                new Customer(null, "Dennis", "dennis@mail.com")
        );
    }

}

package com.github.mysql.dev;

import com.github.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class ApplicationDevMode {

    public static void main(String[] args) {
        SpringApplication
                .from(Application::main)
                .with(MySQLDBContainerDevMode.class)
                .run(args);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class MySQLDBContainerDevMode {

        @Bean
        @RestartScope
        @ServiceConnection(name = "mysql")
        public MySQLContainer<?> mysql() {
            return new MySQLContainer<>("mysql:8");
        }
    }

}

package com.github.mysql.dev;

import com.github.Application;
import org.springframework.boot.SpringApplication;

public class LocalDevTestApplication {


    public static void main(String[] args) {
        SpringApplication
                .from(Application::main)
                .with(LocalDevTestcontainersConfig.class)
                .run(args);
    }

}

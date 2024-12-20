package com.creatorhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.creatorhub")
public class
CreatorHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(CreatorHubApplication.class, args);
    }
}
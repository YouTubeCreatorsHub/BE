package com.creatorhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CreatorHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(CreatorHubApplication.class, args);
    }
}
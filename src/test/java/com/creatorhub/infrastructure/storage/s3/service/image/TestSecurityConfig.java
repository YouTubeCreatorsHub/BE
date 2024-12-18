package com.creatorhub.infrastructure.storage.s3.service.image;

import com.creatorhub.infrastructure.storage.s3.S3BucketPolicyConfigurer;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class TestSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/resources/**").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    @Primary
    public S3BucketPolicyConfigurer s3BucketPolicyConfigurer(S3Client s3Client) {
        return new S3BucketPolicyConfigurer(s3Client) {
            @PostConstruct
            @Override
            public void configureBucketPolicy() {
                // 테스트에서는 S3 버킷 정책 설정을 건너뜀
            }
        };
    }
}
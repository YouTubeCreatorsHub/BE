package com.creatorhub.infrastructure.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/boards/**").hasRole("ADMIN")  // 게시판 생성은 ADMIN만
                        .requestMatchers(HttpMethod.GET, "/api/v1/boards/**").permitAll()        // 게시판 조회는 모두 허용
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/boards/**").hasRole("ADMIN") // 게시판 삭제는 ADMIN만
                        .requestMatchers(HttpMethod.PUT, "/api/v1/boards/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
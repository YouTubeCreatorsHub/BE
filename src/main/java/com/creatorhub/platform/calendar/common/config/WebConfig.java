package com.creatorhub.platform.calendar.common.config;

import com.creatorhub.platform.calendar.adapter.in.web.converter.StringToCalendarEventIdConverter;
import com.creatorhub.platform.calendar.adapter.in.web.converter.StringToCalendarIdConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToCalendarIdConverter());
        registry.addConverter(new StringToCalendarEventIdConverter());
    }
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // API 엔드포인트에 대해
                .allowedOrigins("http://localhost:3000")  // 프론트엔드 서버 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true);
    }
}
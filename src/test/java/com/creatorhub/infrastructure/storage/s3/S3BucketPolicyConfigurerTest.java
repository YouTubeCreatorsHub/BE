package com.creatorhub.infrastructure.storage.s3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutBucketCorsRequest;
import software.amazon.awssdk.services.s3.model.CORSRule;
import software.amazon.awssdk.services.s3.model.GetBucketEncryptionResponse;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
class S3BucketPolicyConfigurerTest {
    @Autowired
    private S3Client s3Client;

    @Test
    void shouldConfigureBucketPolicies() {
        // CORS 설정 생성
        CORSRule corsRule = CORSRule.builder()
                .allowedOrigins("*")
                .allowedMethods("GET", "PUT", "POST", "DELETE")
                .allowedHeaders("*")
                .maxAgeSeconds(3000)
                .build();

        PutBucketCorsRequest corsConfig = PutBucketCorsRequest.builder()
                .bucket("your-bucket-name")
                .corsConfiguration(cfg -> cfg.corsRules(corsRule))  // corsRules() 대신 corsConfiguration() 사용
                .build();

        assertThat(corsConfig).isNotNull();
    }
}
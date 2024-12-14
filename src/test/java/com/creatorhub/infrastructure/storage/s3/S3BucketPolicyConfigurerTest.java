package com.creatorhub.infrastructure.storage.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class S3BucketPolicyConfigurerTest {

    @MockBean
    private S3Client s3Client;

    @Autowired
    private S3BucketPolicyConfigurer s3BucketPolicyConfigurer;

    @BeforeEach
    void setUp() {
        when(s3Client.putBucketPolicy((Consumer<PutBucketPolicyRequest.Builder>) any()))
                .thenReturn(PutBucketPolicyResponse.builder().build());
        when(s3Client.putBucketCors((Consumer<PutBucketCorsRequest.Builder>) any()))
                .thenReturn(PutBucketCorsResponse.builder().build());
        when(s3Client.putBucketEncryption((Consumer<PutBucketEncryptionRequest.Builder>) any()))
                .thenReturn(PutBucketEncryptionResponse.builder().build());
        when(s3Client.putBucketLifecycleConfiguration((Consumer<PutBucketLifecycleConfigurationRequest.Builder>) any()))
                .thenReturn(PutBucketLifecycleConfigurationResponse.builder().build());

        // Reset invocations after bean initialization
        reset(s3Client);
    }

    @Test
    void shouldConfigureBucketPolicies() {
        // when
        s3BucketPolicyConfigurer.configureBucketPolicy();

        // then
        verify(s3Client, times(1)).putBucketPolicy((Consumer<PutBucketPolicyRequest.Builder>) any());
        verify(s3Client, times(1)).putBucketCors((Consumer<PutBucketCorsRequest.Builder>) any());
        verify(s3Client, times(1)).putBucketEncryption((Consumer<PutBucketEncryptionRequest.Builder>) any());
        verify(s3Client, times(1)).putBucketLifecycleConfiguration((Consumer<PutBucketLifecycleConfigurationRequest.Builder>) any());
    }
}

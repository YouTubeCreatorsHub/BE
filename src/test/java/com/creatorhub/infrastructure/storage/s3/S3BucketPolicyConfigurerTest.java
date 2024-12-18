package com.creatorhub.infrastructure.storage.s3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutBucketCorsResponse;
import software.amazon.awssdk.services.s3.model.PutBucketEncryptionResponse;
import software.amazon.awssdk.services.s3.model.PutBucketLifecycleConfigurationResponse;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyResponse;

import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3BucketPolicyConfigurerTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3BucketPolicyConfigurer s3BucketPolicyConfigurer;

    @Test
    void shouldConfigureBucketPolicies() {
        // given
        when(s3Client.putBucketPolicy(any(Consumer.class)))
                .thenReturn(PutBucketPolicyResponse.builder().build());
        when(s3Client.putBucketCors(any(Consumer.class)))
                .thenReturn(PutBucketCorsResponse.builder().build());
        when(s3Client.putBucketEncryption(any(Consumer.class)))
                .thenReturn(PutBucketEncryptionResponse.builder().build());
        when(s3Client.putBucketLifecycleConfiguration(any(Consumer.class)))
                .thenReturn(PutBucketLifecycleConfigurationResponse.builder().build());

        // when
        s3BucketPolicyConfigurer.configureBucketPolicy();

        // then
        verify(s3Client).putBucketPolicy(any(Consumer.class));
        verify(s3Client).putBucketCors(any(Consumer.class));
        verify(s3Client).putBucketEncryption(any(Consumer.class));
        verify(s3Client).putBucketLifecycleConfiguration(any(Consumer.class));
    }
}
package com.creatorhub.infrastructure.storage.s3;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Component
@RequiredArgsConstructor
public class S3BucketPolicyConfigurer {
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @PostConstruct
    public void configureBucketPolicy() {
        setupBucketPolicy();
        setupBucketCors();
        setupBucketEncryption();
        setupBucketLifecycle();
    }

    private void setupBucketPolicy() {
        String policy = """
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Sid": "PublicReadGetObject",
                        "Effect": "Allow",
                        "Principal": "*",
                        "Action": "s3:GetObject",
                        "Resource": "arn:aws:s3:::%s/*",
                        "Condition": {
                            "IpAddress": {
                                "aws:SourceIp": [
                                    "0.0.0.0/0"
                                ]
                            }
                        }
                    }
                ]
            }
            """.formatted(bucketName);

        s3Client.putBucketPolicy(req -> req
                .bucket(bucketName)
                .policy(policy));
    }

    private void setupBucketCors() {
        CORSRule corsRule = CORSRule.builder()
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "HEAD")
                .allowedOrigins("*")
                .maxAgeSeconds(3600)
                .build();

        s3Client.putBucketCors(req -> req
                .bucket(bucketName)
                .corsConfiguration(cfg -> cfg
                        .corsRules(corsRule)));
    }

    private void setupBucketEncryption() {
        s3Client.putBucketEncryption(req -> req
                .bucket(bucketName)
                .serverSideEncryptionConfiguration(cfg -> cfg
                        .rules(rule -> rule
                                .applyServerSideEncryptionByDefault(defaults -> defaults
                                        .sseAlgorithm(ServerSideEncryption.AES256)))));
    }

    private void setupBucketLifecycle() {
        LifecycleRuleFilter filter = LifecycleRuleFilter.builder()
                .prefix("")
                .build();

        Transition transition = Transition.builder()
                .storageClass(TransitionStorageClass.GLACIER)
                .days(90)
                .build();

        LifecycleRule rule = LifecycleRule.builder()
                .id("ExpireOldFiles")
                .filter(filter)
                .transitions(transition)
                .status(ExpirationStatus.ENABLED)
                .build();

        s3Client.putBucketLifecycleConfiguration(req -> req
                .bucket(bucketName)
                .lifecycleConfiguration(cfg -> cfg
                        .rules(rule)));
    }
}
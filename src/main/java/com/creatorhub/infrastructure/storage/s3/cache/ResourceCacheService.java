package com.creatorhub.infrastructure.storage.s3.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ResourceCacheService {

    private static final String RESOURCE_CACHE = "resourceCache";
    private static final String URL_CACHE = "urlCache";

    @Cacheable(value = RESOURCE_CACHE, key = "#fileName")
    public byte[] getCachedFile(String fileName, Supplier<byte[]> fileLoader) {
        return fileLoader.get();
    }

    @Cacheable(value = URL_CACHE, key = "#fileName")
    public String getCachedUrl(String fileName, Supplier<String> urlGenerator) {
        return urlGenerator.get();
    }

    @CacheEvict(value = {RESOURCE_CACHE, URL_CACHE}, key = "#fileName")
    public void evictCache(String fileName) {
        // 캐시 삭제
    }
}
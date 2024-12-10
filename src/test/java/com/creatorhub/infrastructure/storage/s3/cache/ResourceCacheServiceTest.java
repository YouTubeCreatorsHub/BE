package com.creatorhub.infrastructure.storage.s3.cache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.creatorhub.infrastructure.storage.s3.S3StorageAdapter;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ResourceCacheServiceTest {
    @Autowired
    private ResourceCacheService cacheService;

    @MockBean
    private S3StorageAdapter storageAdapter;

    @Test
    void shouldCacheFileUrl() {
        // given
        String fileName = "test.jpg";
        String expectedUrl = "https://test-bucket.s3.amazonaws.com/test.jpg";
        when(storageAdapter.getFileUrl(fileName)).thenReturn(expectedUrl);

        // when - urlGenerator가 실제 URL을 반환하도록 수정
        String cachedUrl1 = cacheService.getCachedUrl(fileName, () -> storageAdapter.getFileUrl(fileName));
        String cachedUrl2 = cacheService.getCachedUrl(fileName, () -> storageAdapter.getFileUrl(fileName));

        // then
        assertThat(cachedUrl1).isEqualTo(expectedUrl);
        assertThat(cachedUrl2).isEqualTo(expectedUrl);
        verify(storageAdapter, times(1)).getFileUrl(fileName);  // 캐시가 동작하므로 한 번만 호출되어야 함
    }
}
package com.creatorhub.application.resource.port.in;

import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.dto.ResourceSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface GetResourceUseCase {
    ResourceResponse getResource(UUID id);
    Page<ResourceResponse> getAllResources(Pageable pageable);
    Page<ResourceResponse> searchResources(ResourceSearchCriteria criteria, Pageable pageable);
}
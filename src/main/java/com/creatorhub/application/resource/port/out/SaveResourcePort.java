package com.creatorhub.application.resource.port.out;

import com.creatorhub.domain.resource.entity.ResourceEntity;
import java.util.UUID;

public interface SaveResourcePort {
    ResourceEntity save(ResourceEntity resource);
    void delete(UUID id);
}
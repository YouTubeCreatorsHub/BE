package com.creatorhub.application.resource.port.in;

import java.util.UUID;

public interface DeleteResourceUseCase {
    void deleteResource(UUID id);
}
package com.creatorhub.application.resource.port.in;

import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.dto.CreateResourceCommand;

public interface CreateResourceUseCase {
    ResourceResponse createResource(CreateResourceCommand command);
}
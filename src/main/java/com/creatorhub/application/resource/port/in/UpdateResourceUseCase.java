package com.creatorhub.application.resource.port.in;

import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.dto.UpdateResourceCommand;
import java.util.UUID;

public interface UpdateResourceUseCase {
    ResourceResponse updateResource(UUID id, UpdateResourceCommand command);
}

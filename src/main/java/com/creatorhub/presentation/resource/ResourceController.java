package com.creatorhub.presentation.resource;

import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.service.ResourceService;
import com.creatorhub.infrastructure.persistence.resource.mapper.ResourceMapper;
import com.creatorhub.presentation.resource.dto.CreateResourceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;
    private final ResourceMapper resourceMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceResponse createResource(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "request", required = false) CreateResourceRequest request) {
        var command = resourceMapper.toCommand(request, file);
        return resourceService.createResource(command);
    }

    @GetMapping("/{id}")
    public ResourceResponse getResource(@PathVariable UUID id) {
        return resourceService.getResource(id);
    }

    @GetMapping
    public Page<ResourceResponse> getAllResources(Pageable pageable) {
        return resourceService.getAllResources(pageable);
    }

    @DeleteMapping("/{id}")
    public void deleteResource(@PathVariable UUID id) {
        resourceService.deleteResource(id);
    }
}
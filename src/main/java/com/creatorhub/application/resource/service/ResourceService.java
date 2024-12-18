package com.creatorhub.application.resource.service;

import com.creatorhub.application.resource.dto.CreateResourceCommand;
import com.creatorhub.application.resource.dto.ResourceResponse;
import com.creatorhub.application.resource.dto.ResourceSearchCriteria;
import com.creatorhub.application.resource.dto.UpdateResourceCommand;
import com.creatorhub.application.resource.validator.FileValidator;
import com.creatorhub.application.resource.port.in.CreateResourceUseCase;
import com.creatorhub.application.resource.port.in.DeleteResourceUseCase;
import com.creatorhub.application.resource.port.in.GetResourceUseCase;
import com.creatorhub.application.resource.port.in.UpdateResourceUseCase;
import com.creatorhub.application.resource.port.out.FileStoragePort;
import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import com.creatorhub.domain.resource.ImageThumbnailGenerator;
import com.creatorhub.domain.resource.entity.ResourceEntity;
import com.creatorhub.domain.resource.repository.ResourceRepository;
import com.creatorhub.domain.resource.vo.FileMetadata;
import com.creatorhub.domain.resource.vo.ResourceMetadata;
import com.creatorhub.infrastructure.persistence.resource.entity.ResourceJpaEntity;
import com.creatorhub.infrastructure.persistence.resource.mapper.ResourceMapper;
import com.creatorhub.infrastructure.persistence.resource.specification.ResourceSpecification;
import com.creatorhub.infrastructure.storage.s3.FileMetadataExtractor;
import com.creatorhub.domain.resource.ImageThumbnailGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResourceService implements CreateResourceUseCase, GetResourceUseCase,
        UpdateResourceUseCase, DeleteResourceUseCase {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final Optional<FileStoragePort> fileStoragePort;
    private final FileValidator fileValidator;
    private final FileMetadataExtractor metadataExtractor;
    private final ImageThumbnailGenerator thumbnailGenerator;
    private final FileUploadProgressSender progressSender;

    @Override
    @Transactional
    public ResourceResponse createResource(CreateResourceCommand command) {
        ResourceEntity entity = resourceMapper.toEntity(command);
        String fileName = null;

        if (command.getFile() != null) {
            fileValidator.validateFileSize(command.getFile());

            try {
                // 파일 메타데이터 추출
                FileMetadata metadata = metadataExtractor.extractMetadata(command.getFile());
                entity.updateMetadata(ResourceMetadata.builder()
                        .format(metadata.getMimeType())
                        .size(metadata.getSize())
                        .build());

                if (fileStoragePort.isPresent()) {
                    fileName = generateFileName(command);
                    byte[] fileContent = command.getFile().getBytes();

                    // 초기 진행상황 전송
                    progressSender.sendProgress(fileName, 0, fileContent.length, null);

                    // 원본 파일 업로드
                    String fileUrl = fileStoragePort.get().uploadFile(fileName, fileContent);
                    entity.updateUrl(fileUrl);

                    // 이미지인 경우 썸네일 생성 및 업로드
                    if (metadata.getMimeType().startsWith("image/")) {
                        try {
                            byte[] thumbnailContent = thumbnailGenerator.generateThumbnail(fileContent);
                            String thumbnailFileName = "thumbnails/" + fileName;
                            String thumbnailUrl = fileStoragePort.get().uploadFile(thumbnailFileName, thumbnailContent);
                            entity.updateThumbnailUrl(thumbnailUrl);

                            // 썸네일 생성 완료 상태 전송
                            progressSender.sendProgress(fileName, fileContent.length, fileContent.length, thumbnailUrl);
                        } catch (Exception e) {
                            progressSender.sendError(fileName, "썸네일 생성 실패");
                            throw new BusinessException(ErrorCode.THUMBNAIL_GENERATION_FAILED);
                        }
                    } else {
                        // 이미지가 아닌 경우 업로드 완료 상태 전송
                        progressSender.sendProgress(fileName, fileContent.length, fileContent.length, null);
                    }
                }
            } catch (IOException e) {
                if (fileName != null) {
                    progressSender.sendError(fileName, "파일 업로드 실패");
                }
                throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
            }
        }

        ResourceEntity savedEntity = resourceRepository.save(entity);
        return resourceMapper.toResponse(savedEntity);
    }

    @Override
    public ResourceResponse getResource(UUID id) {
        ResourceEntity entity = resourceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        return resourceMapper.toResponse(entity);
    }

    @Override
    public Page<ResourceResponse> getAllResources(Pageable pageable) {
        return resourceRepository.findAll(pageable)
                .map(resourceMapper::toResponse);
    }

    @Override
    @Transactional
    public ResourceResponse updateResource(UUID id, UpdateResourceCommand command) {
        ResourceEntity entity = resourceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        entity.update(command.getName(), command.getType(), command.getLicenseType());
        return resourceMapper.toResponse(resourceRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteResource(UUID id) {
        ResourceEntity entity = resourceRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        if (entity.getUrl() != null && fileStoragePort.isPresent()) {
            // 원본 파일 삭제
            String fileName = extractFileNameFromUrl(entity.getUrl());
            fileStoragePort.get().deleteFile(fileName);

            // 썸네일이 있는 경우 썸네일도 삭제
            if (entity.getThumbnailUrl() != null) {
                String thumbnailFileName = extractFileNameFromUrl(entity.getThumbnailUrl());
                fileStoragePort.get().deleteFile(thumbnailFileName);
            }
        }

        resourceRepository.deleteById(id);
    }

    private String generateFileName(CreateResourceCommand command) {
        if (command.getFile() == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "파일이 없습니다.");
        }
        String originalFilename = command.getFile().getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);
        return UUID.randomUUID().toString() + "." + extension;
    }

    private String extractFileNameFromUrl(String url) {
        if (url == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT, "URL이 없습니다.");
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }

    @Override
    public Page<ResourceResponse> searchResources(ResourceSearchCriteria criteria, Pageable pageable) {
        return resourceRepository.searchResources(criteria, pageable)
                .map(resourceMapper::toResponse);
    }
}
package com.creatorhub.domain.resource;

import com.creatorhub.domain.common.error.BusinessException;
import com.creatorhub.domain.common.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Component
public class ImageThumbnailGenerator implements ThumbnailGenerator{
    private static final int THUMBNAIL_WIDTH = 300;
    private static final int THUMBNAIL_HEIGHT = 300;

    public byte[] generateThumbnail(byte[] originalImage) {
        try {
            // 원본 이미지를 BufferedImage로 변환
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(originalImage));
            if (original == null) {
                throw new BusinessException(ErrorCode.INVALID_IMAGE_FORMAT);
            }

            // 썸네일 크기 계산
            Dimension dimension = calculateDimension(original.getWidth(), original.getHeight());

            // 썸네일 생성
            BufferedImage thumbnail = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = thumbnail.createGraphics();

            // 이미지 품질 향상 설정
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 이미지 그리기
            graphics.drawImage(original, 0, 0, dimension.width, dimension.height, null);
            graphics.dispose();

            // 썸네일을 바이트 배열로 변환
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "JPEG", outputStream);

            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("Failed to generate thumbnail", e);
            throw new BusinessException(ErrorCode.THUMBNAIL_GENERATION_FAILED);
        }
    }

    private Dimension calculateDimension(int width, int height) {
        double ratio = (double) height / width;

        if (width > height) {
            return new Dimension(THUMBNAIL_WIDTH, (int) (THUMBNAIL_WIDTH * ratio));
        } else {
            return new Dimension((int) (THUMBNAIL_HEIGHT / ratio), THUMBNAIL_HEIGHT);
        }
    }
}
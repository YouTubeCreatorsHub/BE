package com.creatorhub.infrastructure.storage.s3.service.image;

import com.creatorhub.domain.resource.ImageThumbnailGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
class ImageThumbnailGeneratorTest {
    @Autowired
    private ImageThumbnailGenerator thumbnailGenerator;

    @Test
    void shouldGenerateThumbnail() throws IOException {
        byte[] originalImage = getTestImage();
        byte[] thumbnail = thumbnailGenerator.generateThumbnail(originalImage);

        assertThat(thumbnail).isNotNull();
        assertThat(thumbnail.length).isLessThan(originalImage.length);

        BufferedImage thumbnailImage = ImageIO.read(new ByteArrayInputStream(thumbnail));
        assertThat(thumbnailImage.getWidth()).isLessThanOrEqualTo(300);
        assertThat(thumbnailImage.getHeight()).isLessThanOrEqualTo(300);
    }

    private byte[] getTestImage() throws IOException {
        return getClass().getResourceAsStream("/test-image.jpg").readAllBytes();
    }
}
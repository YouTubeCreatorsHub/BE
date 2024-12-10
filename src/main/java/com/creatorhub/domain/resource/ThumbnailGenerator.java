package com.creatorhub.domain.resource;

import java.io.IOException;

public interface ThumbnailGenerator {
    byte[] generateThumbnail(byte[] originalImage) throws IOException;
}

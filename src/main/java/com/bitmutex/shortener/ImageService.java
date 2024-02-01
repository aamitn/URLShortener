// ImageService interface
package com.bitmutex.shortener;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String uploadImage(MultipartFile file);
    ResponseEntity<byte[]> getImageContent(Long imageId);
}
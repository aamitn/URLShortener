package com.bitmutex.shortener;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:63342")
@RequestMapping("/api/images")

public class ImageController {

    @Autowired
    private ImageService imageService;



    @PostMapping("/upload")
    public ResponseEntity<Object> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String imageUrl = imageService.uploadImage(file);

        if (imageUrl != null) {
            // Get the server name and port from the request
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();
            String proto = request.getScheme();

            // Construct the full image URL
            String fullImageUrl = proto + "://" + serverName + ":" + serverPort + imageUrl;

            // Create a response object
            Map<String, String> response = new HashMap<>();
            response.put("location", fullImageUrl);

            // Return the response as JSON
            return ResponseEntity.ok(response);
        } else {
            // Return an error response
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to upload image.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getImageById(@PathVariable Long imageId) {
        ResponseEntity<byte[]> image = imageService.getImageContent(imageId);
        return image;
    }
}
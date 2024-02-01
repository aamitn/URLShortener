package com.bitmutex.shortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public String uploadImage(MultipartFile file) {
        try {
            // Generate a random image ID and check if it's unique
            long randomImageId;
            do {
                randomImageId = generateUniqueImageId();
            } while (imageRepository.existsById(randomImageId));

            // Create a new ImageEntity
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setId(randomImageId);
            imageEntity.setFilename(file.getOriginalFilename());
            imageEntity.setContentType(file.getContentType());
            imageEntity.setContent(file.getBytes());

            // Save the ImageEntity to the database
            ImageEntity savedImage = imageRepository.save(imageEntity);


            // Return the URL for accessing the image content
            return "/api/images/" + savedImage.getId();
        } catch (IOException e) {
            // Handle the exception (e.g., log or throw a custom exception)
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ResponseEntity<byte[]> getImageContent(Long imageId) {
        Optional<ImageEntity> optionalImage = imageRepository.findById(imageId);

        if (optionalImage.isPresent()) {
            ImageEntity image = optionalImage.get();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG); // Set the appropriate content type

            // Return the image data along with headers
            return new ResponseEntity<>(image.getContent(), headers, HttpStatus.OK);
        }

        return ResponseEntity.notFound().build();
    }

    private static long generateUniqueImageId() {
        try {
            // Use SHA-256 hash for generating a unique ID
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[16];
            random.nextBytes(bytes);
            byte[] hashBytes = digest.digest(bytes);

            // Convert the byte array to a numeric value (long)
            long numericValue = 0;
            for (byte hashByte : hashBytes) {
                numericValue = numericValue * 256 + (hashByte & 0xFF);
            }

            // Ensure a fixed length (12 digits)
            long twelveDigitId = Math.abs(numericValue) % 1_000_000_000_000L;

            // If needed, pad with leading zeros to ensure 12 digits
            return twelveDigitId;
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception (e.g., log or throw a custom exception)
            e.printStackTrace();
            return 0;
        }
    }
}

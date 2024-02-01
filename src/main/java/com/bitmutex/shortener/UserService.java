package com.bitmutex.shortener;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface UserService {


    UserEntity registerNewUser(RegistrationRequest registrationRequest);

    UserEntity updateUserDetailsByUsername(String username, UpdateUserRequest updateUserRequest);

    UserDetailsDto getUserDetailsByUsername(String username);

    UserEntity save(UserEntity userEntity);

    UserEntity findByEmail(String email);

    @NotNull Optional<UserEntity> findById(Long id);

    Optional<UserEntity> findByUsername(String username);

    UserEntity findByResetToken(String resetToken);

    UserEntity updateUserProfilePictureByUsername(String username, MultipartFile file) throws IOException;

    ResponseEntity<byte[]> getProfilePictureByUsername (String  username);
    String getUsernameById(Long userId);

}
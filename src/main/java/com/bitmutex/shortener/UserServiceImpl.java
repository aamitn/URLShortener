package com.bitmutex.shortener;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private EmailService emailService;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }



    @Override
    public UserEntity registerNewUser(RegistrationRequest registrationRequest) {
        UserEntity userEntity = null;
        try {
            // Check if the username or email is already taken (you may want to add more checks)
            if (userRepository.existsByUsername(registrationRequest.getUsername())) {
                throw new RegistrationException("Username is already taken");
            }

            if (userRepository.existsByEmail(registrationRequest.getEmail())) {
                throw new RegistrationException("Email is already taken");
            }

            // Create a new user entity
            userEntity = new UserEntity();
            userEntity.setUsername(registrationRequest.getUsername());
            userEntity.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            userEntity.setEmail(registrationRequest.getEmail());
            userEntity.setFirstName(registrationRequest.getFirstName());
            userEntity.setLastName(registrationRequest.getLastName());
            userEntity.setEnabled(false);

            // You can set additional fields and handle other registration logic here

            // Save the user to the database
            userRepository.save(userEntity);

            // Create a new subscription for the user
            subscriptionService.createSubscription(userEntity, "Free");

            //Send Email
            String Subject = "Hi" + registrationRequest.getFirstName() + "Welcome to Link Shortener";
            String Message = "Welcome to Link Shortener, your username is :" + registrationRequest.getUsername();
            emailService.sendMail(registrationRequest.getEmail(), Subject, Message);


            log.info("Successfully registered user with username:"+userEntity.getUsername()+"and id : "+userEntity.getId());
        } catch (Exception ex) {
           log.error(ex.getMessage());
        }
        return userEntity;
    }


    public UserEntity updateUserProfilePictureByUsername(String username, MultipartFile file) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        try {
            if (!file.isEmpty())
            {
                int sizeLimit = 5 * 1024 * 1024; // Set 5mb max limit
                if(file.getSize() > sizeLimit)
                    throw new Exception("Filesize Limit Exceeded , File Size : " +file.getSize()  + "Size Limit:"+sizeLimit);
                String contentType = file.getContentType();
                if (isValidImageFormat(contentType)) {
                    user.setProfilePicture(file.getBytes());
                    return userRepository.save(user);
                } else {
                    log.error("Invalid file format for profile picture: " + contentType);
                }
            }
        } catch (Exception e) {
            log.error("Profile Picture Update Failed ", e);
        }

        return user;
    }

    private boolean isValidImageFormat(String contentType) {
        return contentType != null && (contentType.equals(MediaType.IMAGE_PNG_VALUE) ||
                contentType.equals(MediaType.IMAGE_JPEG_VALUE) ||
                contentType.equals("image/webp") ||
                contentType.equals("image/bmp") ||
                contentType.equals("image/gif"));
    }


    public ResponseEntity<byte[]> getProfilePictureByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG); // Set the appropriate content type

            // Return the image data along with headers
            return new ResponseEntity<>(user.getProfilePicture(), headers, HttpStatus.OK);
    }


    @SneakyThrows
    @Override
    public UserEntity updateUserDetailsByUsername(String username, UpdateUserRequest request) {

            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            System.out.println(userRepository.existsByEmailAndUsernameNot(request.getEmail(), username));

            // Check if another user with the same email already exists
            String newEmail = request.getEmail();
            if (newEmail != null && userRepository.existsByEmailAndUsernameNot(newEmail, username)) {
                log.error("Email  Update Failed for user :" + username);
                throw new DuplicateEmailException("Another user with the same email already exists.");
            }

            // Check if another user with the same phone already exists
            String phoneNumber = request.getPhoneNumber();
            if (phoneNumber != null && userRepository.existsByPhoneNumberAndUsernameNot(phoneNumber, username)) {
                log.error("phone number  Update Failed for user :" + username);
                throw new DuplicatePhoneNumberException("Another user with the same Phone Number already exists.");
            }


            // Check if another user with the same username already exists excluding the current user
            Long userId = user.getId();
            if (username != null && userRepository.existsByUsernameExcludingCurrentUser(username,userId)) {
                log.error("Username  Update Failed for user :" + username);
                throw new RegistrationException("Another user with the same Phone Number already exists.");
            }


            // Update optional fields if present
            //request.getProfilePicture().ifPresent(user::setProfilePicture);
            // request.getPhoneNumber().ifPresent(user::setPhoneNumber);
            // request.getEmail().ifPresent(user::setEmail);  // Update other fields as needed

            //  Optional.ofNullable(request.getProfilePicture()).ifPresent(user::setProfilePicture);
            Optional.ofNullable(request.getPhoneNumber()).ifPresent(user::setPhoneNumber);
            Optional.ofNullable(request.getUsername()).ifPresent(user::setUsername);
            Optional.ofNullable(request.getPassword()).map(passwordEncoder::encode).ifPresent(user::setPassword);
            Optional.ofNullable(request.getEmail()).ifPresent(user::setEmail);
            Optional.ofNullable(request.getFirstName()).ifPresent(user::setFirstName);
            Optional.ofNullable(request.getLastName()).ifPresent(user::setLastName);


       /* if(request.getProfilePicture()!=null)
            user.setProfilePicture(request.getProfilePicture());
        if(request.getPhoneNumber()!=null)
            user.setPhoneNumber(request.getPhoneNumber());
        if(request.getPassword()!=null)
            user.setPassword ( passwordEncoder.encode ( request.getPassword() ) ); */
            userRepository.save(user);
            log.info("Successfully updated Profile records for user" + username);
            emailService.sendMail(request.getEmail(), "Profile Details Updated","You have successfully updated your profile details for username : "+username+"\n");
            return user;
    }

    public UserDetailsDto getUserDetailsByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        UserDetailsDto userDetails = new UserDetailsDto();
        userDetails.setUserId(user.getId());
        userDetails.setUsername(user.getUsername());
        userDetails.setEmail(user.getEmail());
        userDetails.setFirstName(user.getFirstName());
        userDetails.setLastName(user.getLastName());
        userDetails.setEnabled(user.isEnabled());
        userDetails.setAccountNonLocked(true); // You can customize this based on your logic
        userDetails.setPhoneNumber(user.getPhoneNumber());
        userDetails.setProfilePicture(user.getProfilePicture());
        // Set other fields

        return userDetails;
    }



    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public @NotNull Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }
    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    @Override
    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }


    @Override
    public String getUsernameById(Long userId) {
        // Implement logic to retrieve username by user ID from the data source
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        return userOptional.map(UserEntity::getUsername).orElse(null);
    }
    @Override
    public UserEntity findByResetToken(String resetToken) {
        return userRepository.findByResetToken(resetToken);
    }
}
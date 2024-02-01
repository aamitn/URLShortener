package com.bitmutex.shortener;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;


// UserController.java
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegistrationRequest registrationRequest, HttpServletRequest request) {
        try {
            String generatedOtp =  otpService.generateAndStoreOtp(registrationRequest.getEmail());
            emailService.sendMail(registrationRequest.getEmail(),"OTP FOR REG VERIFICATION","OTP FPR REGISTRATION VERIFICATION IS:"+generatedOtp);
            UserEntity newUser = userService.registerNewUser(registrationRequest);


             URL url = new URL(request.getRequestURL().toString());
             String verificationUrl = new  URL(url.getProtocol(), url.getHost(), url.getPort(), "").toString();
             verificationUrl.concat("?verify-registration?email="+registrationRequest.getEmail());

            return ResponseEntity.ok("User registered successfully with ID: " + newUser.getId()+"\nplease verify your email at : "+verificationUrl);
        } catch (RegistrationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }


    @GetMapping("/details/{username}")
    public ResponseEntity<UserDetailsDto> getUserDetails(@PathVariable String username) {
        try {
            UserDetailsDto userDetails = userService.getUserDetailsByUsername(username);
            return ResponseEntity.ok(userDetails);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUserProfile(@RequestParam String username, @RequestBody UpdateUserRequest updateUserRequest) {
        // Save the updated user
        try {
            userService.updateUserDetailsByUsername(username,updateUserRequest);
            return ResponseEntity.ok("User profile updated successfully");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        catch (RegistrationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User Profile Update Failed as another user with same username already exists");
        }
        catch (DuplicateEmailException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User Profile Update Failed as another user with same email already exists");
        }
        catch (DuplicatePhoneNumberException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User Profile Update Failed as another user with same Phone Number already exists");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unhandled Exception");
        }
    }

    @PostMapping("/profile-picture")
    public ResponseEntity<String> updateUserProfilePicture(@RequestParam String username, @RequestParam("file") MultipartFile file) {
        // Save the updated user
        try {
            userService.updateUserProfilePictureByUsername(username,file);
            return ResponseEntity.ok("User profile picture updated successfully");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/profile-picture")
    public ResponseEntity<byte[]> getUserProfilePicture(@RequestParam String username) {
        try {
            ResponseEntity<byte[]> image = userService.getProfilePictureByUsername(username);
            return image;
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/{userId}/username")
    public ResponseEntity<String> getUsernameById(@PathVariable Long userId) {
        String username = userService.getUsernameById(userId);

        if (username != null) {
            return ResponseEntity.ok(username);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
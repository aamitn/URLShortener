package com.bitmutex.shortener;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class VerificationController {

    private final UserService userService;

    private final UserRepository userRepository;

    private final OtpService otpService;

    public VerificationController(UserService userService, UserRepository userRepository, OtpService otpService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.otpService = otpService;
    }


    @GetMapping("/verify-registration")
    public String showVerificationPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "verify-registration";
    }


    @PostMapping("/verify-registration")
    public ResponseEntity<String>  verifyRegistration(@RequestParam String otp, @RequestParam String email, Model ignoredModel) {

       //String email = model.getAttribute("email").toString();

        Optional<String> storedOtp = otpService.getOtp(email);

        // Verify OTP
        if (storedOtp.isPresent() && storedOtp.get().equals(otp)) {
            // OTP is valid, proceed with user registration
            UserEntity user = userRepository.findByEmail(email);
            user.setEnabled(true);
            otpService.removeOtpByEmail(email);
            userRepository.save(user);
            return ResponseEntity.ok("User verified successfully with ID: " +user.getId() +"\nyou can now login with username"+user.getUsername() );
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("you entered the wrong OTP");
        }
    }
}
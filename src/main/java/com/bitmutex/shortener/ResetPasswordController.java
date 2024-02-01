package com.bitmutex.shortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/reset-password")
public class ResetPasswordController {

    @Autowired
    private UserService userService; // Assuming you have a UserService
    public static PasswordEncoder passwordEncoder;

    public ResetPasswordController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        UserEntity user = userService.findByResetToken(token);

        if (user != null && user.getResetTokenExpiryDateTime().isAfter(LocalDateTime.now())) {
            // Validate the reset token and its expiration
            model.addAttribute("token", token);
            return "reset-password";
        } else {
            // Token is invalid or expired
            return "redirect:/forgot-password?error";
        }
    }

    @PostMapping
    public String processResetPassword(@RequestParam("token") String token, @RequestParam("password") String newPassword, Model model) {
        UserEntity user = userService.findByResetToken(token);

        if (user != null && user.getResetTokenExpiryDateTime().isAfter(LocalDateTime.now())) {

            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                // New password is the same as the old password
                return "redirect:/forgot-password?same";
            }

            // Logic to validate the reset token and set the new password
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setResetToken(null);
            user.setResetTokenExpiryDateTime(null);
            userService.save(user);

            // For simplicity, let's assume the password is reset successfully
            return "redirect:/login?passwordResetSuccess";
        } else {
            // Token is invalid or expired
            return "redirect:/forgot-password?error";
        }
    }
}
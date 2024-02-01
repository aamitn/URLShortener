package com.bitmutex.shortener;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/forgot-password")
public class ForgotPasswordController {

    private final UserService userService;

    @Autowired
    public ForgotPasswordController(UserService userService) {
        this.userService = userService;
    }


    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;


    @GetMapping
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping
    public String processForgotPassword(@RequestParam("email") String email, Model model, HttpServletRequest request) {
        UserEntity user = userService.findByEmail(email);

        if (user != null) {
            // Generate a reset token and set its expiration time
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String resetToken = UUID.randomUUID().toString();
            LocalDateTime resetTokenExpiryDateTime = LocalDateTime.now().plusHours(24);
            user.setResetToken(resetToken);
            user.setResetTokenExpiryDateTime(resetTokenExpiryDateTime);
            userService.save(user);

            // Send an email to the user with the reset link
            sendResetPasswordEmail(user.getEmail(), user.getResetToken(), baseUrl);


            // For simplicity, let's assume the email is sent successfully
            return "redirect:/forgot-password?success";
        } else {
            model.addAttribute("error", "User not found");
            return "forgot-password";
        }
    }

    private void sendResetPasswordEmail(String toEmail, String resetToken, String baseUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(toEmail);
        message.setSubject("Reset Your Password");
        String resetLink = baseUrl + "/reset-password?token=" + resetToken;
        message.setText("To reset your password, click the link below:\n" + resetLink);
        javaMailSender.send(message);
    }
}
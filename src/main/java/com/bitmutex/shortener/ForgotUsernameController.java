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

@Controller
@RequestMapping("/forgot-username")
public class ForgotUsernameController {

    private final UserService userService;

    @Autowired
    public ForgotUsernameController(UserService userService, JavaMailSender javaMailSender) {
        this.userService = userService;
        this.javaMailSender = javaMailSender;
    }


    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @GetMapping
    public String showForgotUsernameForm() {
        return "forgot-username";
    }

    @PostMapping
    public String processForgotUsername(@RequestParam("email") String email, Model model,  HttpServletRequest request) {
        UserEntity user = userService.findByEmail(email);
        String loginUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/login";

        if (user != null) {
            // Send an email to the user with their username
            sendForgotUsernameEmail(user.getEmail(), user.getUsername(), loginUrl);

            // For simplicity, let's assume the email is sent successfully
            return "redirect:/forgot-username?success";
        } else {
            model.addAttribute("error", "User not found");
            return "forgot-username";
        }
    }

    public void sendForgotUsernameEmail(String to, String username, String loginUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject("Forgot Username");
        String messageBody = "Your username is: " + username + "\nPlease login via this link :" + loginUrl;
        message.setText(messageBody);
        javaMailSender.send(message);
    }
}
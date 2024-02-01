package com.bitmutex.shortener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class LoginController {

    @GetMapping("/login")
    String login() {
        return "login";
    }


    @GetMapping("/register")
    public String register() {
        return "register"; // Thymeleaf template name without the extension
    }

}
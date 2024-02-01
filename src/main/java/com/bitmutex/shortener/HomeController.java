package com.bitmutex.shortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;


@RequestMapping("/")
@Controller
public class HomeController {


    @Autowired
    private UserService userService;

    @GetMapping("/")
    String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {

            String username = authentication.getName();

            System.out.println("YOUR USERNAME FROM AUTH CONTEXT"+ username);

            UserDetailsDto user = userService.getUserDetailsByUsername(username);
            UserEntity userElevated = userService.findByEmail(user.getEmail());

            System.out.println("YOUR USERNAME FROM USERENTITY CONTEXT"+ user.getUsername());

            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("userelevated", userElevated);
        }
        return "index";
    }
}

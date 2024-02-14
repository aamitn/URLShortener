package com.bitmutex.shortener;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequestMapping("/")
@Controller
public class HomeController {


    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

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

package com.bitmutex.shortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class MonitoringController {
    private final HealthEndpoint healthEndpoint;
    private final InfoEndpoint infoEndpoint;
    private final UserService userService;


    public MonitoringController(HealthEndpoint healthEndpoint, InfoEndpoint infoEndpoint, UserService userService) {
        this.healthEndpoint = healthEndpoint;
        this.infoEndpoint = infoEndpoint;
        this.userService = userService;

    }

    @GetMapping("/monitoring")
    public String monitoringPanel(Authentication authentication,Model model) {

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            UserDetailsDto user = userService.getUserDetailsByUsername(username);
            UserEntity userElevated = userService.findByEmail(user.getEmail());
            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("userelevated", userElevated);
        }

        // Retrieve Actuator data
        String healthData = healthEndpoint.health().toString();
        String infoData = infoEndpoint.info().toString();

        System.out.println( healthData);
        System.out.println( infoData);

        // Add data to the model

        model.addAttribute("healthData", healthData);
        model.addAttribute("infoData", infoData);

        // Return the monitoring panel template
        return "monitoring";
    }
}

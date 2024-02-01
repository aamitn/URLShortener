package com.bitmutex.shortener;

import com.bitmutex.shortener.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    @PostMapping("/change")
    public ResponseEntity<?> changeSubscription(@RequestParam String username, @RequestParam String newPlanName) {
        try {
            UserEntity userEntity = userService.findByUsername(username)
                         .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            if (userEntity == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found with username: " + username);
            }

            subscriptionService.changeSubscription(userEntity, newPlanName);

            return ResponseEntity.ok("Subscription changed successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error changing subscription: " + ex.getMessage());
        }
    }


    @GetMapping("/details/{username}")
    public ResponseEntity<Object> getSubscriptionDetails(@PathVariable String username) {
        try {
            // Find the user by username
            UserEntity userEntity = userService.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            if (userEntity == null) {
                return ResponseEntity.notFound().build();
            }

            // Get the subscription details for the user
            Map<String, Object> subscriptionDetails = subscriptionService.getCurrentSubscriptionDetails(userEntity);

            return ResponseEntity.ok(subscriptionDetails);
        } catch (Exception e) {
            // Handle exceptions and return an appropriate response
            return ResponseEntity.status(500).body("Internal Server Error");
        }
    }
}
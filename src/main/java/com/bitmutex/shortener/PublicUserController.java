package com.bitmutex.shortener;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user/public/check")
public class PublicUserController {

    private final UserRepository userRepository;

    public PublicUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/username")
    public ResponseEntity<Map<String, Boolean>> checkUsernameExists(@RequestParam String username) {
        boolean usernameExists = userRepository.existsByUsername(username);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", usernameExists);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        boolean emailExists = userRepository.existsByEmail(email);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", emailExists);

        return ResponseEntity.ok(response);
    }
}
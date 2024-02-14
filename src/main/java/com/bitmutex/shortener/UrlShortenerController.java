package com.bitmutex.shortener;

import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/url")
public class UrlShortenerController {

    @Value("${server.port}")  // Inject the server port from application properties
    private String serverPort;

    @Value("${server.servlet.context-path:}")  // Inject the context path from application properties
    private String contextPath;

    private final UrlShortenerService service;

    private final RateLimiter rateLimiter;

    private final UrlShortenerRepository urlShortenerRepository;

    private final UserRepository userRepository;

    @Value("${cooldown.duration}")
    private long cooldownSeconds;

    private final UserService userService;

    private final ConcurrentHashMap<String, Long> cooldownMap = new ConcurrentHashMap<>();

    public UrlShortenerController(UrlShortenerService service, RateLimiter rateLimiter, UrlShortenerRepository urlShortenerRepository, UserRepository userRepository, UserService userService) {
        this.service = service;
        this.rateLimiter = rateLimiter;
        this.urlShortenerRepository = urlShortenerRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }


    @PostMapping("/shorten")
    public ResponseEntity<String> shortenUrl(@RequestBody String originalUrl, Model model) {
        try {
            String clientId = getClientId();

            if (isOnCooldown(clientId)) {
                model.addAttribute("error", "Rate limit exceeded. Cooling down.");
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded. Cooling down.");
            }

            if (rateLimiter.tryAcquire()) {
                // SERVICE CALL
                String shortUrl = service.shortenUrl(originalUrl);
                return ResponseEntity.ok(shortUrl);
            } else {
                setCooldown(clientId);
                model.addAttribute("error", "Rate limit exceeded. Cooldown initiated.");
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded. Cooldown initiated.");
            }
        } catch (UrlShortenerException e) {
            model.addAttribute("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/original")
    public ResponseEntity<String> getOriginalUrl(@RequestParam String shortUrl) {
        try {
            String originalUrl = service.getOriginalUrl(shortUrl);
            return ResponseEntity.ok(originalUrl);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeShortUrl(@RequestParam String shortUrl) {
        try {
            service.removeShortUrl(shortUrl);
            return ResponseEntity.ok("Short URL removed successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

    @GetMapping("/qr/generate")
    public ResponseEntity<String> generateAndRetrieveQrCode(@RequestParam String shortUrl,@RequestParam String logoPath,@RequestParam String fgColor,@RequestParam String bgColor) {
        try {
            String qrCode = service.generateAndRetrieveQrCode(shortUrl,logoPath,fgColor,bgColor);
            return ResponseEntity.ok(qrCode);
        } catch (UrlShortenerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the request.");
        }
    }

    @GetMapping("/qr/image/{shortUrl}")
    public ResponseEntity<byte[]> getQrCode(@PathVariable String shortUrl) {
        try {
            UrlShortener urlShortener = urlShortenerRepository.findByShortUrl(shortUrl);

            if (urlShortener != null && urlShortener.getQrCode() != null) {
                // Decode the Base64-encoded QR code
                byte[] qrCodeBytes = Base64.getDecoder().decode(urlShortener.getQrCode());

                return ResponseEntity
                        .ok()
                        .contentType(MediaType.IMAGE_PNG)
                        .body(qrCodeBytes);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/bio/create")
    public ResponseEntity<String> createBioPage(@RequestBody Map<String, String> requestBody, HttpServletRequest request, Model model) {
        try {
            String clientId = getClientId();

            if (isOnCooldown(clientId)) {
                model.addAttribute("error", "Rate limit exceeded. Cooling down.");
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded. Cooling down.");
            }

            if (rateLimiter.tryAcquire()) {
                String customHtmlContent = requestBody.get("customHtmlContent");
                UrlShortener bioPage = service.createBioPage(customHtmlContent);

                // Determine the protocol (HTTP or HTTPS) dynamically
                String protocol = request.isSecure() ? "https" : "http";

                // Get the server name/ip
                String serverName = request.getServerName();

                // Construct the bio page URL
                String bioPageUrl = protocol + "://" + serverName + ":" + serverPort + contextPath + "/" + bioPage.getShortUrl();

                return ResponseEntity.ok(bioPageUrl);
            } else {
                setCooldown(clientId);
                model.addAttribute("error", "Rate limit exceeded. Cooldown initiated.");
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Rate limit exceeded. Cooldown initiated.");
            }
        }  catch (UrlShortenerException e) {
            model.addAttribute("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @PutMapping("/bio/edit")
    public ResponseEntity<String> editBioPageContent(@RequestParam String shortUrl, @RequestBody Map<String, String> requestBody) {
        try {
            String customHtmlContent = requestBody.get("customHtmlContent");
            UrlShortener editedBioPage = service.editBioPageContent(shortUrl, customHtmlContent);
            return ResponseEntity.ok("Bio page content edited successfully");
        } catch (UrlShortenerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the request.");
        }
    }

    @GetMapping("bio/content")
    public String getBioContent(String shortUrl) {
        // Fetch the bio content from the url_shortener table using the shortUrl
        UrlShortener urlShortener = service.findByShortUrl(shortUrl);

        if (urlShortener != null) {
            return urlShortener.getBioContent();
        } else {
            return "Bio content not found for the given short URL.";
        }
    }


    @GetMapping("/getUrlsByUsername")
    public ResponseEntity<List<UrlShortener>> getUrlsByUsername(@RequestParam String username) {
        try {
            // Step 2: Extract the username parameter from the request

            // Step 3: Use the username to retrieve the corresponding user_id from the user_entity table
            Optional<UserEntity> userEntityOptional = userRepository.findByUsername(username);
            if (userEntityOptional.isPresent()) {
                UserEntity userEntity = userEntityOptional.get();
                Long userId = userEntity.getId();

                // Step 4: Use the obtained user_id to fetch the URLs from the url_shortener table
                List<UrlShortener> urls = service.getUrlsByUserId(userId);

                // Step 5: Return the URLs details as a response
                return ResponseEntity.ok(urls);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // User not found
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/details")
    public ResponseEntity getUrlDetails(@RequestParam String shortUrl, Authentication authentication, Model model) {
        try {
            String name = authentication.getName();

            UserDetailsDto user = userService.getUserDetailsByUsername(name);
            UserEntity userElevated = userService.findByEmail(user.getEmail());

            UrlShortener urlShortener = service.getUrlDetailsByShortUrl(shortUrl);

            model.addAttribute("user",user);
            model.addAttribute("userElevated",userElevated);



            if (urlShortener != null) {
                UrlDetailsDTO urlDetailsDTO = new UrlDetailsDTO(
                        urlShortener.getId(),
                        urlShortener.getUserId(),
                        urlShortener.getOriginalUrl(),
                        urlShortener.getShortUrl(),
                        urlShortener.getLinkType(),
                        urlShortener.getHits(),
                        urlShortener.getUniqueHits(),
                        urlShortener.getTimestamp(),
                        urlShortener.getQrCode(),
                        urlShortener.getBioContent()
                );
                return ResponseEntity.status(HttpStatus.OK).body(urlDetailsDTO); // User not found
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // User not found
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editUrl(
            @RequestParam String oldShortUrl,
            @RequestParam String newShortUrl) {

        // Check if the new short URL already exists
        UrlShortener existingUrl = service.findByShortUrl(newShortUrl);
        if (existingUrl != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("New short URL already exists.");
        }

        // Proceed with the update if the new short URL is unique
        UrlShortener urlToEdit = service.findByShortUrl(oldShortUrl);
        if (urlToEdit != null) {
            urlToEdit.setShortUrl(newShortUrl);
            service.saveOrUpdate(urlToEdit);
            return ResponseEntity.ok("URL updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Old short URL not found.");
        }
    }


    @GetMapping("/status")
    public ResponseEntity<Integer> getLinkStatus(@RequestParam String shortUrl) {
        int linkStatus = service.getLinkStatusByShortUrl(shortUrl);
        return new ResponseEntity<>(linkStatus, HttpStatus.OK);
    }

    @GetMapping("/setstatus")
    public ResponseEntity<String> setLinkStatus(
            @RequestParam String shortUrl,
            @RequestParam int linkStatus) {
        service.setLinkStatusByShortUrl(shortUrl, linkStatus);
        return new ResponseEntity<>("Link status set successfully", HttpStatus.OK);
    }


    @PostMapping("/set-password")
    public ResponseEntity<String> setPassword(
            @RequestParam String shortUrl,
            @RequestParam(required = false) String password
    ) {
        UrlShortener urlShortener = service.findByShortUrl(shortUrl);

        if (urlShortener == null) {
            return new ResponseEntity<>("Short URL not found", HttpStatus.NOT_FOUND);
        }

        // Set the password (or set it to null to remove the password)
        urlShortener.setPassword(password);

        // Save the updated UrlShortener entity
        service.savePassword(urlShortener);

        return new ResponseEntity<>("Password set successfully", HttpStatus.OK);
    }


    @GetMapping("get-password")
    public ResponseEntity<String> getPassword(
            @RequestParam String shortUrl) {
        String password = service.getPasswordByShortUrl(shortUrl);
        return new ResponseEntity<>(password, HttpStatus.OK);
    }


    @PostMapping("reset-password")
    public ResponseEntity<String> setPasswordToNull(@RequestParam String shortUrl) {
        try {
            service.setPasswordToNull(shortUrl);
            return new ResponseEntity<>("Password set to null successfully / Successful Reset", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error setting password to null: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isOnCooldown(String clientId) {
        Long cooldownEndTime = cooldownMap.get(clientId);
        return cooldownEndTime != null && cooldownEndTime > System.currentTimeMillis();
    }
    private void setCooldown(String clientId) {
        // Set the cooldown period from application properties
        long cooldownDuration = TimeUnit.SECONDS.toMillis(cooldownSeconds);

        // Calculate the cooldown end time
        long cooldownEndTime = System.currentTimeMillis() + cooldownDuration;

        // Set the cooldown end time in the map
        cooldownMap.put(clientId, cooldownEndTime);
    }



    protected String getClientId() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String clientIp = request.getRemoteAddr();
        return "ip_" + clientIp; // Prefixing with "ip_" for clarity
    }
}

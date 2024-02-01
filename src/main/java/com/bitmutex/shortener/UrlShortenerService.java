package com.bitmutex.shortener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;


@Service
@Slf4j
public class UrlShortenerService {

    @Autowired
    private UrlShortenerRepository repository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;  // Inject the HttpServletRequest

    @Value("${server.port}")  // Inject the server port from application properties
    private String serverPort;

    @Value("${server.servlet.context-path:}")  // Inject the context path from application properties
    private String contextPath;

    @Value("${server.base.url}")
    private String serverBaseUrl;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String shortenUrl(String jsonInput) {
        try {
            String originalUrl = extractOriginalUrl(jsonInput);

            // Validate the original URL
            validateOriginalUrl(originalUrl);

            // Ensure the URL has a valid scheme (http:// or https://)
            if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
                originalUrl = "https://" + originalUrl;
            }
            //Check if URL returns 200OK
            if(!isValidHttpResponse(originalUrl))
                throw new UrlShortenerException("Shortening Error: Url does not give 200OK, we won't shorten this");

            // Generate a new shortcode
            String shortCode = generateShortCode(originalUrl);

            // Get the user ID (you need to implement a method to fetch the current user ID)
            Long userId = getCurrentUserId();

            // Set linkType to "short" (as per your requirement)
            String linkType = "short";



            // Get the user entity (you need to implement a method to fetch the current user entity)
            UserEntity userEntity = userService.findById(userId).get();

            // Check if the user has exceeded the maximum short URL limit
            int maxShortUrlLimit =  Integer.parseInt(subscriptionService.getCurrentSubscriptionDetails(userEntity).get("maxShortUrl").toString());
            int currentShortUrls = repository.countShortUrlsByUserId(userEntity.getId());

            if (currentShortUrls >= maxShortUrlLimit) {
                throw new MaxShortUrlLimitExceededException("Maximum short URL limit exceeded for the user.");
            }


            // Save the original, short URL, user ID, and link type to the database
            UrlShortener urlShortener = new UrlShortener();
            urlShortener.setOriginalUrl(originalUrl);
            urlShortener.setShortUrl(shortCode);
            urlShortener.setUserId(userId);
            urlShortener.setLinkType(linkType);
            urlShortener.setLinkStatus(1);
            repository.save(urlShortener);
            log.info("Saved Record to DB");

            // Get the server name/ip
            String serverName = request.getServerName();

            // Construct the complete URL using HTTPS (if applicable)
            String protocol = request.isSecure() ? "https" : "http";
            String completeUrl = protocol + "://" + serverName + ":" + serverPort + contextPath + "/" + shortCode;


            //  log.info("Successfully Shortened URL : " + originalUrl + "  -> " + completeUrl + " [SHORTCODE: " + shortCode + "]");
            log.info("Successfully Shortened URL:From {} to-> {} [SHORTCODE: {}]", originalUrl, completeUrl, shortCode);


            // Return the complete URL
            return completeUrl;
        }catch (MaxShortUrlLimitExceededException e) {
            // Handle the exception as needed
            log.error("Max short URL limit exceeded for the user", e);
            throw e;
        }
        catch(UrlShortenerException e)
        {
            log.error("Shortening Error: Validation Phase");
            throw e;
        }
        catch (Exception e) {
            // Handle other exceptions
            log.error("Shortening Error", e);
            throw new UrlShortenerException("Shortening Error", e);
        }
    }

    private String generateShortCode(String originalUrl) {
        try {
            // Introduce randomness with UUID
            String randomPart = UUID.randomUUID().toString().replaceAll("-", "");

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest((originalUrl + randomPart).getBytes(StandardCharsets.UTF_8));

            // Convert the hash to a URL-friendly string
            String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);

            // Extract only alphabet characters from the encoded hash
            String alphabetChars = encoded.replaceAll("[^a-zA-Z]", "");

            log.info("Shortcode Generation Successful!");

            // Take the first 8 characters as the shortcode
            return alphabetChars.substring(0, Math.min(alphabetChars.length(), 8));
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating short code", e);
            throw new UrlShortenerException("Error generating short code", e);
        }
    }
    private String extractOriginalUrl(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            String originalUrl = jsonNode.get("originalUrl").asText();

            // Validate the format of the original URL
           // validateOriginalUrl(originalUrl);
            log.info("Original URL Extracted from request");
            return originalUrl;
        } catch (Exception e) {
            log.error("Error extracting originalUrl from JSON , BAD JSON Format", e);
            throw new UrlShortenerException("Error extracting originalUrl from JSON , BAD JSON Format", e);
        }
    }
    public String getOriginalUrl(String shortUrl) {
        UrlShortener urlShortener = repository.findByShortUrl(shortUrl);

        // Check if the short URL exists in the database
        if (urlShortener != null) {
            return urlShortener.getOriginalUrl();
        } else {
            // Return null or handle the case when short URL is not found
            log.error("Short URL not found: " + shortUrl);
            throw new UrlShortenerException("Short URL not found: " + shortUrl);
        }
    }

    public void removeShortUrl(String shortUrl) {
        UrlShortener urlShortener = repository.findByShortUrl(shortUrl);

        // Check if the short URL exists in the database
        if (urlShortener != null) {
            repository.delete(urlShortener);
            log.info("SHORTURL:"+shortUrl+"Deleted Successfully!");
        } else {
            // Handle the case when short URL is not found
            log.error("Short URL not found: " + shortUrl);
            throw new UrlShortenerException("Short URL not found: " + shortUrl);
        }
    }

    private void validateOriginalUrl(String originalUrl) {
        if (StringUtils.isEmpty(originalUrl)) {
            log.error("Original URL cannot be empty or null");
            throw new UrlShortenerException("Original URL cannot be empty or null");
        }

        // Regular expression for a simple URL format check
        String urlRegex = "^(https?://)?([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})(/[a-zA-Z0-9-._?%&=]*)?$";

        try {
            // Check if the URL has a scheme, if not, assume it is HTTP
            if (!originalUrl.contains("://")) {
                originalUrl = "http://" + originalUrl;
            }

            // Basic URL format check using regex
            if (!Pattern.matches(urlRegex, originalUrl)) {
                throw new UrlShortenerException("Invalid URL format: " + originalUrl);
            }

            UriComponentsBuilder.fromHttpUrl(originalUrl).build().toUri();
            log.info("URL validation successful for original URL: {}", originalUrl);
        } catch (IllegalArgumentException e) {
            log.error("Invalid URL format: {}", originalUrl, e);
            throw new UrlShortenerException("Invalid URL format: " + originalUrl);
        } catch (Exception e) {
            log.error("Error validating URL: {}", originalUrl, e);
            throw new UrlShortenerException("Error validating URL: " + originalUrl);
        }
    }


    /*private void validateOriginalUrl(String originalUrl) {
        if (StringUtils.isEmpty(originalUrl)) {
            log.error("Original URL cannot be empty or null");
            throw new UrlShortenerException("Original URL cannot be empty or null");
        }

        try {
            new URL(originalUrl).toURI(); // Validate URL format
            log.info("URL Validation Succecssful for original URL:"+originalUrl);
        } catch (MalformedURLException | IllegalArgumentException | URISyntaxException e) {
            log.error("Invalid original URL format: " + originalUrl, e);
            throw new UrlShortenerException("Invalid original URL format: " + originalUrl, e);
        }
    }*/

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null)
        {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                return userDetails.getUserId();
            }
            else if (principal instanceof CustomOAuth2User) {
                UserDetailsDto user = userService.getUserDetailsByUsername(authentication.getName());
                return user.getUserId();
            }
            else
                return null;
        }
        // Return a default value or throw an exception based on your requirements
        return null;
    }

    public String generateAndRetrieveQrCode(String shortUrl, String logoPath, String fgColor, String bgColor) {
        UrlShortener urlShortener = repository.findByShortUrl(shortUrl);

        if (urlShortener != null) {
            // Generate and set the QR code
            urlShortener.generateQrCode(serverBaseUrl,logoPath,fgColor,bgColor );

            // Save the updated entity
            repository.save(urlShortener);

            // Return the QR code
            return urlShortener.getQrCode();
        } else {
            throw new UrlShortenerException("Short URL not found: " + shortUrl);
        }
    }

    public UrlShortener createBioPage(String customHtmlContent) {
        try {
            Long userId = getCurrentUserId();

            // Get the user entity (you need to implement a method to fetch the current user entity)
            UserEntity userEntity = userService.findById(userId).get();

            // Check if the user has exceeded the bio pages limit
            int currentBioPagesCount = repository.countBioPagesByUserId(userId);
            int maxBioPagesLimit =  Integer.parseInt(subscriptionService.getCurrentSubscriptionDetails(userEntity).get("maxBioPages").toString());

            if (currentBioPagesCount >= maxBioPagesLimit) {
                throw new MaxBioPagesLimitExceededException("Exceeded the maximum allowed number of bio pages.");
            }

            // Create a new bio page
            UrlShortener urlShortener = new UrlShortener("N/A", generateShortCode("bio"));
            urlShortener.setUserId(userId);
            urlShortener.setLinkType("bio");
            urlShortener.setLinkStatus(1);
            urlShortener.setBioContent(customHtmlContent);

            // Save the bio page to the database
            return repository.save(urlShortener);
        }catch (MaxBioPagesLimitExceededException e) {
            log.error("Exceeded the maximum allowed number of bio pages.", e);
            throw e;
        }catch (Exception e) {
            log.error("Error creating bio page", e);
            throw new UrlShortenerException("Error creating bio page", e);
        }
    }

    public UrlShortener editBioPageContent(String shortUrl, String newHtmlContent) {
        UrlShortener bioPage = repository.findByShortUrlAndLinkType(shortUrl, "bio");

        if (bioPage != null) {
            bioPage.setBioContent(newHtmlContent);
            return repository.save(bioPage);
        } else {
            throw new UrlShortenerException("Bio page not found for the given short URL: " + shortUrl);
        }
    }

    public List<UrlShortener> getUrlsByUserId(Long userId) {
        try {
            // Assume that UrlDetails is an entity representing the details of a URL
            List<UrlShortener> urlDetailsList = repository.findByUserId(userId);

            // You might need to convert entities to a DTO or customize the response based on your needs
            // For simplicity, let's assume UrlDetails is the entity itself

            return urlDetailsList;
        } catch (Exception e) {
            // Handle exceptions or log them as needed
            throw new UrlShortenerException("Error retrieving URLs by user ID", e);
        }
    }

    public UrlShortener getUrlDetailsByShortUrl(String shortUrl) {
        return repository.findByShortUrl(shortUrl);
    }

    public UrlShortener findByShortUrl(String shortUrl) {
        // Implement this method to fetch UrlShortener entity by shortUrl
        return repository.findByShortUrl(shortUrl);
    }

    public void saveOrUpdate(UrlShortener urlShortener) {
        // Your implementation might vary based on the technology you are using (e.g., Spring Data JPA, Hibernate)

        // Check if the entity already has an ID (existing entity) or not (new entity)
        if (urlShortener.getId() == null) {
            // This is a new entity, save it
            repository.save(urlShortener);
        } else {
            // This is an existing entity, update it
            repository.saveAndFlush(urlShortener);
        }
    }

    public void savePassword(UrlShortener urlShortener) {
        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(urlShortener.getPassword());
        urlShortener.setPassword(hashedPassword);

        // Save the UrlShortener entity
        repository.save(urlShortener);
    }

    public int getLinkStatusByShortUrl(String shortUrl) {
        return repository.getLinkStatusByShortUrl(shortUrl);
    }

    public void setLinkStatusByShortUrl(String shortUrl, int linkStatus) {
        repository.setLinkStatusByShortUrl(shortUrl, linkStatus);
    }
    public boolean validatePassword(String shortUrl, String password) {
        UrlShortener urlShortener = repository.findByShortUrl(shortUrl);
        return passwordEncoder.matches(password, urlShortener.getPassword());
    }

    public void setPasswordByShortUrl(String shortUrl, String password) {
        UrlShortener urlShortener = repository.findByShortUrl(shortUrl);
        urlShortener.setPassword(passwordEncoder.encode(password));
        repository.save(urlShortener);
    }

    public void setPasswordToNull(String shortUrl) {
        UrlShortener urlShortener = repository.findByShortUrl(shortUrl);
        if (urlShortener != null) {
            urlShortener.setPassword(null);
            repository.save(urlShortener);
        } else {
            throw new EntityNotFoundException("UrlShortener not found for shortUrl: " + shortUrl);
        }
    }


    public String getPasswordByShortUrl(String shortUrl) {
        UrlShortener urlShortener = repository.findByShortUrl(shortUrl);
        return urlShortener.getPassword();
    }


    private boolean isValidHttpResponse(String originalUrl) {
        try {
            URL url = new URL(originalUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }
}
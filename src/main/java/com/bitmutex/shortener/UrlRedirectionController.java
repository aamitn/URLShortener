package com.bitmutex.shortener;

import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import jakarta.persistence.EntityManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class UrlRedirectionController {

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @Autowired
    private UrlShortenerService urlShortenerService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("{shortUrl}")
    public String submitPassword(@PathVariable String shortUrl,
                                 @RequestParam String password,
                                 HttpSession session,
                                 Model model) {
        UrlShortener urlShortener = urlShortenerRepository.findByShortUrl(shortUrl);
        // Validate the password (you may want to check it against a stored hash)

        //boolean isValidPassword = urlShortener.getPassword().equals(password);
        // boolean isValidPassword =  passwordEncoder.matches(urlShortener.getPassword(),password);
        boolean isValidPassword = urlShortenerService.validatePassword(shortUrl, password);


        if (isValidPassword) {
            // Store an indicator in the session that the user is authenticated
            session.setAttribute("authenticated", true);

            // Redirect to the original URL
            return "redirect:/" + shortUrl;
        } else {
            // Password is incorrect, redirect back to the password prompt with an error flag
            model.addAttribute("error", true);
            return "password_prompt";
        }
    }

    @GetMapping("/{shortUrl}")
    public Object redirect(@PathVariable String shortUrl,
                           HttpServletResponse response,
                           HttpServletRequest request,
                           @RequestParam(required = false) String password,
                           @RequestParam(required = false) boolean error, // New parameter to indicate password error
                           Model model,
    HttpSession session) throws IOException {
        UrlShortener urlShortener = urlShortenerRepository.findByShortUrl(shortUrl);
        if (urlShortener != null) {
            long startTime = System.nanoTime();

            // Capture analytics data
            Analytics analytics = new Analytics();
            analytics.setUrlShortener(urlShortener);
            analytics.setDeviceIp(getOriginalIp(request));
            //  analytics.setUid(generateUid()); // You need to implement generateUid() method
            analytics.setTimezone(request.getHeader("Time-Zone")); // Assuming you get timezone from header
            analytics.setAcceptTypes(request.getHeader("Accept"));
            analytics.setAcceptLanguage(request.getHeader("Accept-Language"));
            analytics.setDeviceType(request.getHeader("User-Agent")); // Assuming you get device type from User-Agent header
            request.getRemoteUser();
            analytics.setTimestamp(String.valueOf(new Date())); // Assuming you want to capture the timestamp

            // Update visit count or hits
            long hits = getVisitCount(shortUrl);
            urlShortener.setHits(hits);

            // Update unique hits count
            long uniqueHitsCount = getUniqueHitsCount(shortUrl);
            urlShortener.setUniqueHits(uniqueHitsCount);

            urlShortenerRepository.save(urlShortener);

            if (urlShortener.getLinkStatus() == 0) {
                System.out.println(urlShortener.getLinkStatus());
                log.info("Disable link blocked with short URL: " + shortUrl);
                        // Handle case where link is disabled
                return "disabled";
            }
            // Check if the user is authenticated (session contains the 'authenticated' attribute)
            boolean isAuthenticated = session.getAttribute("authenticated") != null;
            if (!isAuthenticated) {

                if (urlShortener.getPassword() != null) {
                    // If the URL is password-protected and no password is provided, show the password form
                    if (password == null) {
                        model.addAttribute("shortUrl", shortUrl);
                        return "password_prompt";
                    }

                    // Check if the provided password is correct
                    if (!urlShortener.getPassword().equals(password)) {
                        model.addAttribute("shortUrl", shortUrl);
                        model.addAttribute("error", true); // Set error attribute
                        return "password_prompt"; // Stay on the password prompt page with error message
                    }
                }
            }
            if ("bio".equals(urlShortener.getLinkType())) {
                // For bio pages, render custom HTML content
                renderBioPage(response, urlShortener);
                log.info("Successful creation of Bio-Page with shortcode: " + shortUrl );
                // Save analytics data to the database
                analyticsRepository.save(analytics);
            }

            else {
                // Redirect to the original URL
                String originalUrl = urlShortener.getOriginalUrl();
                response.sendRedirect(originalUrl);

                // Calculate and set redirection time
                long endTime = System.nanoTime();
                long redirectionTimeNano = endTime - startTime;
                double redirectionTimeMillis = redirectionTimeNano / 1000000f;  // ns to ms 10^-6
                analytics.SetRedirectionTime(redirectionTimeMillis);

                // Save analytics data to the database
                analyticsRepository.save(analytics);

                log.info("Successful Redirection of short URL: " + shortUrl + "to original URL: " + originalUrl + " in " + redirectionTimeMillis + " nanoseconds");
            }
        } else {
            // Handle not found scenario (e.g., show an error page)
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Short URL not found");
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No Allowed Methods Found");
    }
    private String getOriginalIp(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            // The client's original IP address is the first IP in the list
            String originalIp = xForwardedForHeader.split(",")[0].trim();

            // Try to convert IPv6 to IPv4 format
            try {
                InetAddress inetAddress = InetAddress.getByName(originalIp);
                if (inetAddress instanceof java.net.Inet6Address) {
                    // Convert IPv6 to IPv4 format if necessary
                    originalIp = inetAddress.getHostAddress();
                }
            } catch (UnknownHostException e) {
                // Handle the exception, e.g., log or ignore
            }

            return originalIp;
        }
        // If X-Forwarded-For is not present, fall back to the remote address
        return request.getRemoteAddr();
    }
    public long getVisitCount(String shortUrl) {
        UrlShortener urlShortener = urlShortenerRepository.findByShortUrl(shortUrl);

        if (urlShortener != null) {
            return analyticsRepository.countByUrlShortenerId(urlShortener.getId());
        } else {
            throw new UrlShortenerException("Short URL not found: " + shortUrl);
        }
    }

    public long getUniqueHitsCount(String shortUrl) {
        UrlShortener urlShortener = urlShortenerRepository.findByShortUrl(shortUrl);

        if (urlShortener != null) {
            return urlShortenerRepository.countUniqueHitsByUrlShortenerId(urlShortener.getId());
        } else {
            throw new UrlShortenerException("Short URL not found: " + shortUrl);
        }
    }
    private void renderBioPage(HttpServletResponse response, UrlShortener urlShortener) throws IOException {
        // Set content type to HTML
        response.setContentType("text/html;charset=UTF-8");

        // Use custom HTML content for bio-page, or default content if not provided
        String bioContent = urlShortener.getBioContent();
        if (bioContent == null || bioContent.isEmpty()) {
            bioContent = getDefaultBioContent();
        }

        // Write the HTML content to the response
        try (PrintWriter writer = response.getWriter()) {
            writer.write(bioContent);
            writer.flush();
        }
    }

    private String getDefaultBioContent() {
        // Provide default HTML content for bio-pages
        return "<html><head><title>Bio Page</title></head><body><h1>Welcome to the Bio Page!</h1></body></html>";
    }

}
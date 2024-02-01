package com.bitmutex.shortener;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UrlShortenerServiceTest {

    @Mock
    private UrlShortenerRepository repository;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void shortenUrl_InvalidOriginalUrlFormat() {
        // Test
        assertThrows(UrlShortenerException.class, () -> {
            String jsonInput = "{\"originalUrl\":\"invalid-url\"}";
            urlShortenerService.shortenUrl(jsonInput);
        });
    }

    @Test
    void getOriginalUrl_Success() {
        // Mocking dependencies
        when(repository.findByShortUrl(any())).thenReturn(createMockUrlShortener());

        // Test
        String result = urlShortenerService.getOriginalUrl("abc123");

        assertNotNull(result);
        assertEquals("http://example.com", result);
    }

    @Test
    void getOriginalUrl_NotFound() {
        // Mocking dependencies
        when(repository.findByShortUrl(any())).thenReturn(null);

        // Test
        assertThrows(UrlShortenerException.class, () -> {
            urlShortenerService.getOriginalUrl("nonexistent");
        });
    }

    @Test
    void removeShortUrl_Success() {
        // Mocking dependencies
        when(repository.findByShortUrl(any())).thenReturn(createMockUrlShortener());

        // Test
        assertDoesNotThrow(() -> {
            urlShortenerService.removeShortUrl("abc123");
        });
    }

    @Test
    void removeShortUrl_NotFound() {
        // Mocking dependencies
        when(repository.findByShortUrl(any())).thenReturn(null);

        // Test
        assertThrows(UrlShortenerException.class, () -> {
            urlShortenerService.removeShortUrl("nonexistent");
        });
    }

    // Other test methods for the UrlShortenerService...

    // Helper methods to create mock objects for testing
    private UserEntity createMockUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        return userEntity;
    }

    private UrlShortener createMockUrlShortener() {
        UrlShortener urlShortener = new UrlShortener();
        urlShortener.setId(1L);
        urlShortener.setOriginalUrl("http://example.com");
        urlShortener.setShortUrl("abc123");
        urlShortener.setUserId(1L);
        return urlShortener;
    }


}

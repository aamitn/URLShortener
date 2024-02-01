package com.bitmutex.shortener;

import jakarta.servlet.http.HttpSession;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UrlRedirectionControllerTest {

    @Mock
    private UrlShortenerRepository urlShortenerRepository;

    @Mock
    private AnalyticsRepository analyticsRepository;

    @Mock
    private UrlShortenerService urlShortenerService;

    @InjectMocks
    private UrlRedirectionController urlRedirectionController;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSubmitPassword_InvalidPassword() {
        // Arrange
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);
        UrlShortener urlShortener = new UrlShortener();
        urlShortener.setPassword("correctPassword");
        when(urlShortenerRepository.findByShortUrl(anyString())).thenReturn(urlShortener);

        // Act
        String result = urlRedirectionController.submitPassword("shortUrl", "wrongPassword", session, model);

        // Assert
        assert result.equals("password_prompt");
        verify(model).addAttribute(eq("error"), eq(true));
    }
}

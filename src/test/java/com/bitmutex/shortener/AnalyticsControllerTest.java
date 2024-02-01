package com.bitmutex.shortener;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class AnalyticsControllerTest {

    @InjectMocks
    private AnalyticsController analyticsController;

    @Mock
    private AnalyticsService analyticsService;

    @Mock
    private UrlShortenerService urlShortenerService;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.openMocks(this);
        System.out.println("Test started for class: " + AnalyticsController.class.getSimpleName());
    }

    @Test
    public void testGetAnalyticsDataWithValidShortUrl() {
        // Print a message indicating the test passed for the class
        System.out.println("Valid Short Url Case");

        // Given
        String shortUrl = "validShortUrl";
        UrlShortener urlShortener = new UrlShortener();
        urlShortener.setId(1L);

        List<Analytics> expectedAnalyticsList = List.of(new Analytics(), new Analytics());

        // Mocking the behavior of the services
        when(urlShortenerService.findByShortUrl(shortUrl)).thenReturn(urlShortener);
        when(analyticsService.findByUrlShortenerId(urlShortener.getId())).thenReturn(expectedAnalyticsList);

        // When
        List<Analytics> actualAnalyticsList = analyticsController.getAnalyticsData(shortUrl);

        // Then
        assertNotNull(actualAnalyticsList);
        assertEquals(actualAnalyticsList, expectedAnalyticsList);

        // Print a message indicating the test passed for the class
        System.out.println("Test passed for class: " + AnalyticsController.class.getSimpleName());
    }

    @Test
    public void testGetAnalyticsDataWithInvalidShortUrl() {
        // Print a message indicating the test started for the class
        System.out.println("Invalid Short Url Case");


        // Given
        String shortUrl = "invalidShortUrl";

        // Mocking the behavior of the services when shortUrl is not found
        when(urlShortenerService.findByShortUrl(shortUrl)).thenReturn(null);

        // When
        List<Analytics> actualAnalyticsList = analyticsController.getAnalyticsData(shortUrl);

        // Then
        assertNotNull(actualAnalyticsList);
        assertEquals(actualAnalyticsList.size(), 0);
    }
}

package com.bitmutex.shortener;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class AnalyticsTest {

    @Mock
    private UrlShortener urlShortener;

    @BeforeClass
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAnalyticsEntity() {
        // Print a message indicating the test started for the class
        System.out.println("Test started for class: " + Analytics.class.getSimpleName());

        // Create an Analytics instance
        Analytics analytics = new Analytics();
        analytics.setUrlShortener(urlShortener);
        analytics.setDeviceIp("192.168.0.1");
        analytics.setTimezone("UTC");
        analytics.setDeviceType("Desktop");
        analytics.setAcceptLanguage("en-US");
        analytics.setAcceptTypes("text/html");
        analytics.setTimestamp("2022-01-01 12:00:00");
        analytics.SetRedirectionTime(5.0);

        // Verify the state of the Analytics instance
        assertNotNull(analytics.getUrlShortener());
        assertEquals("192.168.0.1", analytics.getDeviceIp());
        assertEquals("UTC", analytics.getTimezone());
        assertEquals("Desktop", analytics.getDeviceType());
        assertEquals("en-US", analytics.getAcceptLanguage());
        assertEquals("text/html", analytics.getAcceptTypes());
        assertEquals("2022-01-01 12:00:00", analytics.getTimestamp());
        assertEquals((Object) 5.0, analytics.getRedirectionTime()); // Cast double to Object

        // Print a message indicating the test passed
        System.out.println("Test passed for class: " + Analytics.class.getSimpleName());
    }
}

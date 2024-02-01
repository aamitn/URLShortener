package com.bitmutex.shortener;

import com.bitmutex.shortener.Analytics;
import com.bitmutex.shortener.AnalyticsRepository;
import com.bitmutex.shortener.AnalyticsService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

public class AnalyticsServiceTest {

    @Mock
    private AnalyticsRepository analyticsRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindByUrlShortenerId() {
        // Given
        Long urlShortenerId = 1L;
        Analytics analytics1 = createAnalytics("192.168.0.1");
        Analytics analytics2 = createAnalytics("192.168.0.2");

        when(analyticsRepository.findByUrlShortenerId(urlShortenerId)).thenReturn(Arrays.asList(analytics1, analytics2));

        // When
        List<Analytics> result = analyticsService.findByUrlShortenerId(urlShortenerId);

        // Then
        assertEquals(result.size(), 2);
        assertEquals(result.get(0), analytics1);
        assertEquals(result.get(1), analytics2);
    }

    private Analytics createAnalytics(String deviceIp) {
        Analytics analytics = new Analytics();
        analytics.setDeviceIp(deviceIp);
        // Set other necessary properties
        return analytics;
    }
}

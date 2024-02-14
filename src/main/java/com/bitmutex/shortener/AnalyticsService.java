// AnalyticsService.java

package com.bitmutex.shortener;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public List<Analytics> findByUrlShortenerId(Long urlShortenerId) {
        // Implement this method to fetch analytics entries based on urlShortenerId
        return analyticsRepository.findByUrlShortenerId(urlShortenerId);
    }

}
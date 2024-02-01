// AnalyticsService.java

package com.bitmutex.shortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    public List<Analytics> findByUrlShortenerId(Long urlShortenerId) {
        // Implement this method to fetch analytics entries based on urlShortenerId
        return analyticsRepository.findByUrlShortenerId(urlShortenerId);
    }

}
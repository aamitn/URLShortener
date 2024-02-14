// AnalyticsController.java

package com.bitmutex.shortener;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    private AnalyticsRepository analyticsRepository;

    private final UrlShortenerService urlShortenerService;

    public AnalyticsController(AnalyticsService analyticsService, UrlShortenerService urlShortenerService) {
        this.analyticsService = analyticsService;
        this.urlShortenerService = urlShortenerService;
    }

    @GetMapping("/api/url/analytics")
    @ResponseBody
    public List<Analytics> getAnalyticsData(@RequestParam String shortUrl) {
        UrlShortener urlShortener = urlShortenerService.findByShortUrl(shortUrl);

        if (urlShortener != null) {
            Long urlShortenerId = urlShortener.getId();
            return analyticsService.findByUrlShortenerId(urlShortenerId);
        } else {
            return List.of(); // Return an empty list if the short URL is not found
        }
    }

}
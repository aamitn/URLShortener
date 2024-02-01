// AnalyticsController.java

package com.bitmutex.shortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    private AnalyticsRepository analyticsRepository;

    @Autowired
    private UrlShortenerService urlShortenerService;

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
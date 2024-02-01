package com.bitmutex.shortener;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Value("${requests.per.second}")
    private double permitsPerSecond;
    @Bean
    public RateLimiter rateLimiter() {
        return RateLimiter.create(permitsPerSecond); // Adjust the rate (permits per second) using the configured value
    }
}

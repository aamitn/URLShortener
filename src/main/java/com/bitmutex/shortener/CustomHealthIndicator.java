package com.bitmutex.shortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CustomHealthIndicator extends AbstractHealthIndicator {

    @Autowired
    StatusCheckService statusCheckService;

    public CustomHealthIndicator(StatusCheckService statusCheckService) {
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        // Add custom health check logic here
        int isCustomComponentHealthy = performCustomCheck();

        if (isCustomComponentHealthy == 1 ) {
            builder.up().withDetail("message", "Internal Custom status check component is healthy");
        } else {
            builder.down().withDetail("message", "Internal Custom status check component NOT healthy");
        }
    }

    private int performCustomCheck() {
        Map<String, Integer> myMap = statusCheckService.getServerStatus();
        return myMap.get("server_status");
    }
}

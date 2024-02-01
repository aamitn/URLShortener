package com.bitmutex.shortener;

public class SubscriptionPlanNotFoundException extends RuntimeException {
    public SubscriptionPlanNotFoundException(String message) {
        super(message);
    }
}
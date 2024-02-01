package com.bitmutex.shortener;

public class MaxShortUrlLimitExceededException extends RuntimeException {

    public MaxShortUrlLimitExceededException() {
        super("Maximum short URL limit exceeded for the user.");
    }

    public MaxShortUrlLimitExceededException(String message) {
        super(message);
    }

    public MaxShortUrlLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
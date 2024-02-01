package com.bitmutex.shortener;

public class MaxBioPagesLimitExceededException extends RuntimeException {

    public MaxBioPagesLimitExceededException(String message) {
        super(message);
    }

    public MaxBioPagesLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
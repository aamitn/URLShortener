package com.bitmutex.shortener;

public class UrlShortenerException extends RuntimeException {

    public UrlShortenerException(String message) {
        super(message);
    }

    public UrlShortenerException(String message, Throwable cause) {
        super(message, cause);
    }
}
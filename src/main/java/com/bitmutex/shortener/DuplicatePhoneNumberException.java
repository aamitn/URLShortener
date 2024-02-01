
package com.bitmutex.shortener;

public class DuplicatePhoneNumberException extends RuntimeException {

    public DuplicatePhoneNumberException(String message) {
        super(message);
    }

    public DuplicatePhoneNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}




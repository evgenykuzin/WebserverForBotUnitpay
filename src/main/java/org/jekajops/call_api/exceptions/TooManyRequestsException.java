package org.jekajops.call_api.exceptions;

public class TooManyRequestsException extends HttpException {
    public TooManyRequestsException(String message) {
        super(message);
    }

    public TooManyRequestsException() {
        this("Response message was: 'too many requests' and code was: 429");
    }
}

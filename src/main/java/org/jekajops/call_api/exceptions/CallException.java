package org.jekajops.call_api.exceptions;

public abstract class CallException extends Exception {
    public CallException(String message) {
        super(message);
    }

    public CallException() {
    }
}

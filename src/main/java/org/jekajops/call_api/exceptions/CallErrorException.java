package org.jekajops.call_api.exceptions;

public class CallErrorException extends CallException{
    private String msg;
    public CallErrorException(String message) {
        msg = message;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}

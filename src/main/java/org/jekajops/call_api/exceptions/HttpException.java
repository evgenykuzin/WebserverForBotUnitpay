package org.jekajops.call_api.exceptions;

public class HttpException extends CallException {
    private String msg;
    public HttpException(String message) {
        msg = message;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}

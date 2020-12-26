package org.jekajops.call_api.exceptions;

public class AudioclipAlreadyExistsException extends HttpException{
    public AudioclipAlreadyExistsException(String fileName) {
        super("Audioclip " + fileName + " already exists!");
    }
}

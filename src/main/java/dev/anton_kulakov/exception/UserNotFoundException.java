package dev.anton_kulakov.exception;

public class UserNotFoundException extends MainAppException {
    public UserNotFoundException(String message) {
        super(message);
    }
}

package dev.anton_kulakov.exception;

public class UserAlreadyExistsException extends MainAppException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

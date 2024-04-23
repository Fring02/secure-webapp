package com.example.password_encryption.util;

public class InvalidCredentialsException extends Exception {
    public InvalidCredentialsException() {
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}

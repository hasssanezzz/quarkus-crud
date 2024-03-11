package com.example.core.exceptions;

public class CustomValidationError {
    public String path;
    public String message;

    public CustomValidationError(String path, String message) {
        this.path = path;
        this.message = message;
    }
}

package com.example.utils;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {
    public List<CustomValidationError> errors;

    public ErrorResponse() {
        this.errors = new ArrayList<>();
    }

    public int getErrorCount() {
        return errors.size();
    }

    public void addError(String path, String message) {
        errors.add(new CustomValidationError(path, message));
    }
}

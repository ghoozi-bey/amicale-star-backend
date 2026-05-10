package com.amicalestar.backend.exceptions;

import java.util.Map;

public class ValidationException
        extends RuntimeException {

    private final Map<String, String> errors;

    public ValidationException(
            String message
    ) {

        super(message);

        this.errors = null;
    }

    public ValidationException(
            Map<String, String> errors
    ) {

        this.errors = errors;
    }

    public Map<String, String> getErrors() {

        return errors;
    }
}
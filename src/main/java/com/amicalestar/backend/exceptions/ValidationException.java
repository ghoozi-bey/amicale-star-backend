package com.amicalestar.backend.exceptions;

import java.util.Map;

public class ValidationException
        extends RuntimeException {

    private final Map<String, String> errors;

    // === Exception avec message simple ===
    public ValidationException(
            String message
    ) {

        super(message);

        this.errors = null;
    }

    // === Exception contenant plusieurs erreurs de validation ===
    public ValidationException(
            Map<String, String> errors
    ) {

        this.errors = errors;
    }

    // === Récupération des erreurs de validation ===
    public Map<String, String> getErrors() {

        return errors;
    }
}
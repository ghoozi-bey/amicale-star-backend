package com.amicalestar.backend.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // === Gestion des erreurs de validation personnalisées ===
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(
            ValidationException ex
    ) {

        // Retour des erreurs sous forme de map
        if (ex.getErrors() != null) {

            return ResponseEntity
                    .badRequest()
                    .body(ex.getErrors());
        }

        // Retour d’un message simple
        Map<String, String> response =
                new HashMap<>();

        response.put(
                "message",
                ex.getMessage()
        );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    // === Gestion des erreurs runtime ===
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {

        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
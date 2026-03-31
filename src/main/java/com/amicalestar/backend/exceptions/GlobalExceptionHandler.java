package com.amicalestar.backend.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDuplicate(DataIntegrityViolationException ex) {

        Map<String, String> errors = new HashMap<>();

        String message = ex.getMostSpecificCause().getMessage();
        System.out.println("DB ERROR: " + message);

        if (message.contains("email")) {
            errors.put("email", "Email déjà utilisé");
        }
        else if (message.contains("cin")) {
            errors.put("cin", "CIN déjà utilisé");
        }
        else if (message.contains("telephone")) {
            errors.put("telephone", "Téléphone déjà utilisé");
        }
        else if (message.contains("adherents_email")) {
            errors.put("email", "Email déjà utilisé");
        }
        else if (message.contains("adherents_cin")) {
            errors.put("cin", "CIN déjà utilisé");
        }
        else if (message.contains("adherents_telephone")) {
            errors.put("telephone", "Téléphone déjà utilisé");
        }
        else {
            errors.put("error", "Valeur déjà existante");
        }

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleCustomValidation(ValidationException ex) {
        return ResponseEntity.badRequest().body(ex.getErrors());
    }

}
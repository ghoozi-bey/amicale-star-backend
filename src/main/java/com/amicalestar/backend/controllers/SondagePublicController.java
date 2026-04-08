package com.amicalestar.backend.controllers;

import com.amicalestar.backend.services.SondageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/sondages")
@RequiredArgsConstructor
public class SondagePublicController {

    private final SondageService sondageService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(sondageService.getAllSondages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sondageService.getSondageById(id));
    }

}

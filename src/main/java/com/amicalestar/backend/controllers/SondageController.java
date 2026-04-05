package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.CreateSondageRequest;
import com.amicalestar.backend.entities.Sondage;
import com.amicalestar.backend.services.SondageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sondages")
public class SondageController {

    private final SondageService sondageService;

    public SondageController(SondageService sondageService) {
        this.sondageService = sondageService;
    }

    @PostMapping
    public ResponseEntity<Sondage> createSondage(
            @Valid @RequestBody CreateSondageRequest request
    ) {
        return ResponseEntity.ok(sondageService.createSondage(request));
    }
}

package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.CreateSondageRequest;
import com.amicalestar.backend.dto.SondageResponse;
import com.amicalestar.backend.entities.Sondage;
import com.amicalestar.backend.services.SondageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sondages")
@RequiredArgsConstructor
public class SondageController {

    private final SondageService sondageService;

    @PostMapping
    public ResponseEntity<Sondage> createSondage( @RequestBody CreateSondageRequest request, Authentication authentication ) {

        String email = authentication.getName(); // from JWT

        Sondage sondage = sondageService.createSondage(request, email);

        System.out.println("AUTH NAME = " + authentication.getName());

        return ResponseEntity.ok(sondage);

    }

    @GetMapping("/me")
    public ResponseEntity<List<SondageResponse>> getMySondages(Authentication authentication) {
        String email = authentication.getName();

        return ResponseEntity.ok(sondageService.getSondagesByCreatorEmail(email));
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<?> publish(@PathVariable Long id) {
        return ResponseEntity.ok(sondageService.publierSondage(id));
    }

    @PutMapping("/{id}/unpublish")
    public ResponseEntity<?> unpublish(@PathVariable Long id) {
        return ResponseEntity.ok(sondageService.annulerPublication(id));
    }

}
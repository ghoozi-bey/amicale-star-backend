package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.CreateSondageRequest;
import com.amicalestar.backend.entities.Sondage;
import com.amicalestar.backend.services.SondageService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<Sondage>> getAll() {
        return ResponseEntity.ok(sondageService.getAllSondages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sondage> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sondageService.getSondageById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sondage> update(
            @PathVariable Long id,
            @Valid @RequestBody CreateSondageRequest request
    ) {
        return ResponseEntity.ok(sondageService.updateSondage(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sondageService.deleteSondage(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Sondage> publish(@PathVariable Long id) {
        return ResponseEntity.ok(sondageService.publishSondage(id));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Sondage>> getActiveSondages() {
        return ResponseEntity.ok(sondageService.getActiveSondages());
    }
}

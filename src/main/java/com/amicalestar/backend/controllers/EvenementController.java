package com.amicalestar.backend.controllers;

import com.amicalestar.backend.entities.Evenement;
import com.amicalestar.backend.services.EvenementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/api/evenements")
@RequiredArgsConstructor
@CrossOrigin
public class EvenementController {

    private final EvenementService evenementService;

    @PostMapping
    public Evenement create(@RequestBody Evenement evenement) {
        return evenementService.createEvenement(evenement);
    }

    @GetMapping
    public List<Evenement> getAll() {
        return evenementService.getAllEvenements();
    }

    @PutMapping("/archiver/{id}")
    public Evenement archiver(@PathVariable Long id) {
        return evenementService.archiverEvenement(id);
    }
    @GetMapping("/")
    public String test() {
        return "Backend Amicale STAR fonctionne 🚀";
    }
}
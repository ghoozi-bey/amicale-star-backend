package com.amicalestar.backend.controllers;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.services.AdherentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adherents")
@RequiredArgsConstructor
@CrossOrigin
public class AdherentController {

    private final AdherentService adherentService;

    @PostMapping
    public Adherent create(@RequestBody Adherent adherent) {
        return adherentService.createAdherent(adherent);
    }

    @GetMapping
    public List<Adherent> getAll() {
        return adherentService.getAllAdherents();
    }

    @GetMapping("/{matricule}")
    public Adherent getById(@PathVariable String matricule) {
        return adherentService.getAdherentById(matricule);
    }

    @DeleteMapping("/{matricule}")
    public void delete(@PathVariable String matricule) {
        adherentService.deleteAdherent(matricule);
    }

    @PatchMapping("/{matricule}")
    public Adherent update(@PathVariable String matricule, @RequestBody Adherent adherent) {
        return adherentService.updateAdherent(matricule, adherent);
    }

    @GetMapping("/test")
    public String test() {
        return "Adherent API fonctionne 🚀";
    }
}
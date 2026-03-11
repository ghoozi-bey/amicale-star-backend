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

    @GetMapping("/{id}")
    public Adherent getById(@PathVariable Long id) {
        return adherentService.getAdherentById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        adherentService.deleteAdherent(id);
    }

    @PatchMapping("/{id}")
    public Adherent update(@PathVariable Long id, @RequestBody Adherent adherent) {
        return adherentService.updateAdherent(id, adherent);
    }

    @GetMapping("/test")
    public String test() {
        return "Adherent API fonctionne 🚀";
    }
}
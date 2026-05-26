package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.adherent.AdherentDTO;
import com.amicalestar.backend.dto.adherent.CreateUserRequest;
import com.amicalestar.backend.dto.adherent.UpdateUserRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.evenement.TypeEvenement;
import com.amicalestar.backend.enums.Departement;
import com.amicalestar.backend.enums.TypeAdherent;
import com.amicalestar.backend.services.interfaces.AdminUserService;
import com.amicalestar.backend.services.interfaces.TypeEvenementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminUserController {

    // Service de gestion des utilisateurs
    private final AdminUserService adminUserService;

    // Service de gestion des types d'événements
    private final TypeEvenementService typeEvenementService;

    // === Création d’un nouvel utilisateur ===
    @PostMapping("/create-user")
    public Adherent create(@Valid @RequestBody CreateUserRequest request) {

        return adminUserService.createUser(request);
    }

    // === Liste paginée des utilisateurs ===
    @GetMapping("/users")
    public Page<AdherentDTO> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "matricule") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        return adminUserService.getAllUsers(
                page,
                size,
                sortBy,
                sortDir
        );
    }

    // === Suppression d’un utilisateur ===
    @DeleteMapping("/users/{matricule}")
    public void delete(@PathVariable String matricule) {

        adminUserService.deleteUser(matricule);
    }

    // === Récupération d’un utilisateur par matricule ===
    @GetMapping("/users/{matricule}")
    public AdherentDTO getUser(@PathVariable String matricule) {

        return adminUserService.getUserByMatricule(matricule);
    }

    // === Mise à jour des informations utilisateur ===
    @PatchMapping("/users/{matricule}")
    public Adherent updateUser(
            @PathVariable String matricule,
            @RequestBody UpdateUserRequest request
    ) {

        return adminUserService.updateUser(matricule, request);
    }

    // === Liste des départements ===
    @GetMapping("/departements")
    public Departement[] getDepartements() {

        return Departement.values();
    }

    // === Liste des types d’adhérents ===
    @GetMapping("/types-adherent")
    public TypeAdherent[] getTypesAdherent() {

        return TypeAdherent.values();
    }

    // === Liste des types d’événements ===
    @GetMapping("/type-evenements")
    public List<TypeEvenement> getTypeEvenements() {

        return typeEvenementService.getAll();
    }
}
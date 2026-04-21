package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.CreateUserRequest;
import com.amicalestar.backend.dto.UpdateUserRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.TypeEvenement;
import com.amicalestar.backend.enums.Departement;
import com.amicalestar.backend.enums.TypeAdherent;
import com.amicalestar.backend.services.AdminUserService;
import com.amicalestar.backend.services.TypeEvenementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.amicalestar.backend.dto.AdherentDTO;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final TypeEvenementService typeEvenementService;

    @PostMapping("/create-user")
    public Adherent create(@Valid @RequestBody CreateUserRequest request) {
        return adminUserService.createUser(request);
    }

    @GetMapping("/users")
    public List<AdherentDTO> getAll() {
        return adminUserService.getAllUsers();
    }

    @DeleteMapping("/users/{matricule}")
    public void delete(@PathVariable String matricule) {
        adminUserService.deleteUser(matricule);
    }

    @GetMapping("/users/{matricule}")
    public Adherent getUser(@PathVariable String matricule) {
        return adminUserService.getUserByMatricule(matricule);
    }

    @PatchMapping("/users/{matricule}")
    public Adherent updateUser(
            @PathVariable String matricule,
            @RequestBody UpdateUserRequest request
    ) {
        return adminUserService.updateUser(matricule, request);
    }

    @GetMapping("/departements")
    public Departement[] getDepartements() {
        return Departement.values();
    }

    @GetMapping("/types-adherent")
    public TypeAdherent[] getTypesAdherent() {
        return TypeAdherent.values();
    }

    @GetMapping("/type-evenements")
    public List<TypeEvenement> getTypeEvenements() {
        return typeEvenementService.getAll();
    }
}
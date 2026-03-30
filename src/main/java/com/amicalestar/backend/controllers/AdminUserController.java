package com.amicalestar.backend.controllers;

import com.amicalestar.backend.dto.CreateUserRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.services.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminUserController {

    private final AdminUserService adminUserService;

    // ✅ CREATE USER (corrigé)
    @PostMapping("/create-user")
    public Adherent create(@RequestBody CreateUserRequest request) {
        return adminUserService.createUser(request);
    }

    // ✅ READ
    @GetMapping("/users")
    public List<Adherent> getAll() {
        return adminUserService.getAllUsers();
    }

    // ✅ DELETE
    @DeleteMapping("/users/{matricule}")
    public void delete(@PathVariable String matricule) {
        adminUserService.deleteUser(matricule);
    }
}
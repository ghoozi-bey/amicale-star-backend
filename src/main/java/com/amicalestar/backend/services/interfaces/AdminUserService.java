package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.adherent.AdherentDTO;
import com.amicalestar.backend.dto.adherent.CreateUserRequest;
import com.amicalestar.backend.dto.adherent.UpdateUserRequest;
import com.amicalestar.backend.entities.Adherent;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminUserService {

    // === Création d’un utilisateur ===
    Adherent createUser(CreateUserRequest request);

    // === Liste paginée des utilisateurs ===
    Page<AdherentDTO> getAllUsers(
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    // === Recherche d’un utilisateur par matricule ===
    AdherentDTO getUserByMatricule(String matricule);

    // === Suppression d’un utilisateur ===
    void deleteUser(String matricule);

    // === Mise à jour d’un utilisateur ===
    Adherent updateUser(
            String matricule,
            UpdateUserRequest request
    );
}
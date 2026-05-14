package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.adherent.AdherentDTO;
import com.amicalestar.backend.dto.adherent.CreateUserRequest;
import com.amicalestar.backend.dto.adherent.UpdateUserRequest;
import com.amicalestar.backend.entities.Adherent;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AdminUserService {

    Adherent createUser(CreateUserRequest request);

    Page<AdherentDTO> getAllUsers(
            int page,
            int size,
            String sortBy,
            String sortDir
    );

    AdherentDTO getUserByMatricule(String matricule);

    void deleteUser(String matricule);

    Adherent updateUser(String matricule, UpdateUserRequest request);
}
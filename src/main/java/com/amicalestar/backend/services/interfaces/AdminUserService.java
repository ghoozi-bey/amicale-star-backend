package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.adherent.AdherentDTO;
import com.amicalestar.backend.dto.adherent.CreateUserRequest;
import com.amicalestar.backend.dto.adherent.UpdateUserRequest;
import com.amicalestar.backend.entities.Adherent;

import java.util.List;

public interface AdminUserService {

    Adherent createUser(CreateUserRequest request);

    List<AdherentDTO> getAllUsers();

    AdherentDTO getUserByMatricule(String matricule);

    void deleteUser(String matricule);

    Adherent updateUser(String matricule, UpdateUserRequest request);
}
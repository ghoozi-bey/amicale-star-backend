package com.amicalestar.backend.services;

import com.amicalestar.backend.dto.AdherentDTO;
import com.amicalestar.backend.dto.CreateUserRequest;
import com.amicalestar.backend.dto.UpdateUserRequest;
import com.amicalestar.backend.entities.Adherent;

import java.util.List;

public interface AdminUserService {

    Adherent createUser(CreateUserRequest request);

    List<AdherentDTO> getAllUsers();

    Adherent getUserByMatricule(String matricule);

    void deleteUser(String matricule);

    Adherent updateUser(String matricule, UpdateUserRequest request);
}
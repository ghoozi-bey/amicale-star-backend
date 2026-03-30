package com.amicalestar.backend.services;

import com.amicalestar.backend.dto.CreateUserRequest;
import com.amicalestar.backend.entities.Adherent;

import java.util.List;

public interface AdminUserService {
    Adherent createUser(CreateUserRequest request);
    List<Adherent> getAllUsers();
    void deleteUser(String matricule);
}
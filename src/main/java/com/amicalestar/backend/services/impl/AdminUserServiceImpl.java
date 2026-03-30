package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.CreateUserRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.enums.TypeAdherent;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.services.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdherentRepository adherentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Adherent createUser(CreateUserRequest request) {

        Adherent adherent = Adherent.builder()
                .matricule(request.getMatricule())
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .cin(request.getCin())
                .telephone(request.getTelephone())
                .dateNaissance(request.getDateNaissance())
                .departement(request.getDepartement())
                .typeAdherent(
                        request.getTypeAdherent() != null
                                ? request.getTypeAdherent()
                                : TypeAdherent.MEMBRE_AMICALE
                )
                .actif(true)
                .typeEvenement(null) // important
                .build();

        return adherentRepository.save(adherent);
    }

    @Override
    public List<Adherent> getAllUsers() {
        return adherentRepository.findAll();
    }

    @Override
    public void deleteUser(String matricule) {
        adherentRepository.deleteById(matricule);
    }
}
package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.CreateUserRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.TypeEvenement;
import com.amicalestar.backend.enums.TypeAdherent;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.services.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.amicalestar.backend.services.AdherentService;
import com.amicalestar.backend.repositories.TypeEvenementRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdherentRepository adherentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdherentService adherentService;
    private final TypeEvenementRepository typeEvenementRepository;


    @Override
    public Adherent createUser(CreateUserRequest request) {

        TypeEvenement typeEvenement = null;

        if (request.getTypeEvenementId() != null) {
            typeEvenement = typeEvenementRepository
                    .findById(request.getTypeEvenementId())
                    .orElse(null);
        }

        Adherent adherent = Adherent.builder()
                .matricule(request.getMatricule())
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(request.getPassword())
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
                .typeEvenement(typeEvenement)
                .build();

        return adherentService.createAdherent(adherent);
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
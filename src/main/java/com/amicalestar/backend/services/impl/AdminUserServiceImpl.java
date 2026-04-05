package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.CreateUserRequest;
import com.amicalestar.backend.dto.UpdateUserRequest;
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
import com.amicalestar.backend.exceptions.ValidationException;

import java.util.List;
import java.util.HashMap;
import java.util.Map;



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

    @Override
    public Adherent getUserByMatricule(String matricule) {
        return adherentRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Override
    public Adherent updateUser(String matricule, UpdateUserRequest request){

        Adherent user = adherentRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, String> errors = new HashMap<>();

        // ===== DUPLICATE CHECKS =====
        if (request.getEmail() != null &&
                adherentRepository.existsByEmail(request.getEmail()) &&
                !user.getEmail().equals(request.getEmail())) {

            errors.put("email", "Email déjà utilisé");
        }

        if (request.getCin() != null &&
                adherentRepository.existsByCin(request.getCin()) &&
                !user.getCin().equals(request.getCin())) {

            errors.put("cin", "CIN déjà utilisé");
        }

        if (request.getTelephone() != null &&
                adherentRepository.existsByTelephone(request.getTelephone()) &&
                !user.getTelephone().equals(request.getTelephone())) {

            errors.put("telephone", "Téléphone déjà utilisé");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // ===== SIMPLE FIELDS =====
        if (request.getNom() != null) user.setNom(request.getNom());
        if (request.getPrenom() != null) user.setPrenom(request.getPrenom());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getTelephone() != null) user.setTelephone(request.getTelephone());
        if (request.getDepartement() != null) user.setDepartement(request.getDepartement());
        if (request.getDateNaissance() != null) user.setDateNaissance(request.getDateNaissance());
        if (request.getPhotoProfil() != null) user.setPhotoProfil(request.getPhotoProfil());
        if (request.getTypeAdherent() != null) user.setTypeAdherent(request.getTypeAdherent());
        if (request.getActif() != null) user.setActif(request.getActif());
        if (request.getCin() != null) user.setCin(request.getCin());

        // ===== PASSWORD =====
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // ===== TYPE EVENEMENT =====
        // enforce rule
        if (request.getTypeAdherent() != null &&
                request.getTypeAdherent() != TypeAdherent.MEMBRE_AMICALE) {

            user.setTypeEvenement(null);

        } else if (request.getTypeEvenementId() != null) {

            TypeEvenement type = typeEvenementRepository.findById(request.getTypeEvenementId())
                    .orElseThrow(() -> new RuntimeException("TypeEvenement not found"));

            user.setTypeEvenement(type);
        }

        return adherentRepository.save(user);
    }
}
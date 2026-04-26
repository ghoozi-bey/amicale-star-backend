package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.AdherentDTO;
import com.amicalestar.backend.dto.CreateUserRequest;
import com.amicalestar.backend.dto.UpdateUserRequest;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.TypeEvenement;
import com.amicalestar.backend.enums.TypeAdherent;
import com.amicalestar.backend.exceptions.ValidationException;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.repositories.TypeEvenementRepository;
import com.amicalestar.backend.services.AdminUserService;
import com.amicalestar.backend.services.AdherentService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final AdherentRepository adherentRepository;
    private final PasswordEncoder passwordEncoder;
    private final TypeEvenementRepository typeEvenementRepository;

    @Override
    public Adherent createUser(CreateUserRequest request) {

        Map<String, String> errors = new HashMap<>();

        // ===== VALIDATION =====
        if (request.getMatricule() == null || request.getMatricule().isEmpty()) {
            errors.put("matricule", "Matricule obligatoire");
        }

        if (request.getNom() == null || request.getNom().isEmpty()) {
            errors.put("nom", "Nom obligatoire");
        }

        if (request.getPrenom() == null || request.getPrenom().isEmpty()) {
            errors.put("prenom", "Prénom obligatoire");
        }

        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            errors.put("email", "Email invalide");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            errors.put("password", "Mot de passe minimum 6 caractères");
        }

        if (request.getDateNaissance() == null) {
            errors.put("dateNaissance", "Dta de naissance obligatoire");
        }

        if (request.getCin() == null || !request.getCin().matches("\\d{8}")) {
            errors.put("cin", "CIN doit contenir 8 chiffres");
        }

        if (request.getTelephone() == null || !request.getTelephone().matches("\\d{8}")) {
            errors.put("telephone", "Téléphone doit contenir 8 chiffres");
        }

        // ===== DUPLICATES =====
        if (request.getMatricule() != null && adherentRepository.existsByMatricule(request.getMatricule())) {
            errors.put("matricule", "Matricule déjà utilisée");
        }
        if (request.getEmail() != null && adherentRepository.existsByEmail(request.getEmail())) {
            errors.put("email", "Email déjà utilisé");
        }

        if (request.getCin() != null && adherentRepository.existsByCin(request.getCin())) {
            errors.put("cin", "CIN déjà utilisé");
        }

        if (request.getTelephone() != null && adherentRepository.existsByTelephone(request.getTelephone())) {
            errors.put("telephone", "Téléphone déjà utilisé");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        // ===== TYPE EVENEMENT =====
        TypeEvenement typeEvenement = null;

        if (request.getTypeEvenementId() != null) {
            typeEvenement = typeEvenementRepository
                    .findById(request.getTypeEvenementId())
                    .orElse(null);
        }

        // ===== CREATE USER =====
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
                .typeEvenement(typeEvenement)
                .photoProfil(null)
                .photoType(null)
                .build();

        return adherentRepository.save(adherent);
    }

    // ================= READ =================
    @Override
    public List<AdherentDTO> getAllUsers() {
        return adherentRepository.findAll().stream().map(user -> {

            Long typeEvenementId = user.getTypeEvenement() != null
                    ? user.getTypeEvenement().getId()
                    : null;

            AdherentDTO dto = new AdherentDTO(
                    user.getMatricule(),
                    user.getNom(),
                    user.getPrenom(),
                    user.getEmail(),
                    user.getTelephone(),
                    user.getCin(),
                    user.getTypeAdherent().name(),
                    user.getDepartement().name(),
                    "http://localhost:8080/api/user/photo/" + user.getMatricule(),
                    user.getPhotoProfil() != null && user.getPhotoProfil().length > 0
            );

            dto.setTypeEvenementId(typeEvenementId);
            dto.setActif(user.getActif());
            dto.setDateNaissance(
                    user.getDateNaissance() != null
                            ? new java.text.SimpleDateFormat("yyyy-MM-dd")
                            .format(user.getDateNaissance())
                            : null
            );

            return dto;

        }).toList();
    }

    @Override
    public AdherentDTO getUserByMatricule(String matricule) {

        Adherent adherent = adherentRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Long typeEvenementId = null;

        if (adherent.getTypeEvenement() != null) {
            typeEvenementId = adherent.getTypeEvenement().getId();
        }

        AdherentDTO dto = new AdherentDTO(
                adherent.getMatricule(),
                adherent.getNom(),
                adherent.getPrenom(),
                adherent.getEmail(),
                adherent.getTelephone(),
                adherent.getCin(),
                adherent.getTypeAdherent().name(),
                adherent.getDepartement().name(),
                "http://localhost:8080/api/user/photo/" + adherent.getMatricule(),
                adherent.getPhotoProfil() != null && adherent.getPhotoProfil().length > 0
        );

        dto.setTypeEvenementId(typeEvenementId);
        dto.setActif(adherent.getActif());
        dto.setDateNaissance(
                adherent.getDateNaissance() != null
                        ? new java.text.SimpleDateFormat("yyyy-MM-dd")
                        .format(adherent.getDateNaissance())
                        : null
        );

        return dto;
    }

    // ================= DELETE =================
    @Override
    public void deleteUser(String matricule) {
        adherentRepository.deleteById(matricule);
    }

    // ================= UPDATE =================
    @Override
    public Adherent updateUser(String matricule, UpdateUserRequest request){

        Adherent user = adherentRepository.findByMatricule(matricule)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, String> errors = new HashMap<>();

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

        if (request.getNom() != null) user.setNom(request.getNom());
        if (request.getPrenom() != null) user.setPrenom(request.getPrenom());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getTelephone() != null) user.setTelephone(request.getTelephone());
        if (request.getDepartement() != null) user.setDepartement(request.getDepartement());
        if (request.getDateNaissance() != null) user.setDateNaissance(request.getDateNaissance());
        if (request.getTypeAdherent() != null) user.setTypeAdherent(request.getTypeAdherent());
        if (request.getActif() != null) user.setActif(request.getActif());
        if (request.getCin() != null) user.setCin(request.getCin());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getTypeEvenementId() != null) {
            TypeEvenement typeEvenement = typeEvenementRepository
                    .findById(request.getTypeEvenementId())
                    .orElse(null);

            user.setTypeEvenement(typeEvenement);
        } else {
            user.setTypeEvenement(null);
        }

        return adherentRepository.save(user);
    }

}
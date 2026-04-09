package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.enums.TypeAdherent;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.services.AdherentService;
import com.amicalestar.backend.dto.UpdateProfileRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.amicalestar.backend.exceptions.ValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdherentServiceImpl implements AdherentService {

    private final AdherentRepository adherentRepository;
    private final PasswordEncoder passwordEncoder;

    // ================= CREATE =================
    @Override
    public Adherent createAdherent(Adherent adherent) {

        Map<String, String> errors = new HashMap<>();

        if (adherentRepository.existsById(adherent.getMatricule())) {
            errors.put("matricule", "Matricule déjà utilisé");
        }

        if (adherentRepository.existsByEmail(adherent.getEmail())) {
            errors.put("email", "Email déjà utilisé");
        }

        if (adherentRepository.existsByCin(adherent.getCin())) {
            errors.put("cin", "CIN déjà utilisé");
        }

        if (adherentRepository.existsByTelephone(adherent.getTelephone())) {
            errors.put("telephone", "Téléphone déjà utilisé");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        if (adherent.getTypeAdherent() == null) {
            adherent.setTypeAdherent(TypeAdherent.ADHERENT);
        }
        // 🔥 FIX BLOB (IMPORTANT)
        if (adherent.getPhotoProfil() == null) {
            adherent.setPhotoProfil(null);
            adherent.setPhotoType(null);
        }

        adherent.setPassword(passwordEncoder.encode(adherent.getPassword()));

        return adherentRepository.save(adherent);
    }
    @Override
    public Adherent getAdherentById(String matricule) {
        return adherentRepository.findById(matricule).orElse(null);
    }

    // 🔥 IMPORTANT pour controller image
    @Override
    public Adherent getByMatricule(String matricule) {
        return adherentRepository.findById(matricule)
                .orElseThrow(() -> new RuntimeException("Adherent non trouvé"));
    }

    // ================= UPDATE ADMIN =================
    @Override
    public Adherent updateAdherent(String matricule, Adherent adherent) {

        Adherent existing = adherentRepository.findById(matricule).orElse(null);

        if (existing != null) {

            if (adherent.getNom() != null)
                existing.setNom(adherent.getNom());

            if (adherent.getPrenom() != null)
                existing.setPrenom(adherent.getPrenom());

            if (adherent.getEmail() != null)
                existing.setEmail(adherent.getEmail());

            if (adherent.getPassword() != null)
                existing.setPassword(passwordEncoder.encode(adherent.getPassword()));

            if (adherent.getTypeAdherent() != null)
                existing.setTypeAdherent(adherent.getTypeAdherent());

            if (adherent.getDateNaissance() != null)
                existing.setDateNaissance(adherent.getDateNaissance());

            if (adherent.getTelephone() != null)
                existing.setTelephone(adherent.getTelephone());

            if (adherent.getDepartement() != null)
                existing.setDepartement(adherent.getDepartement());

            if (adherent.getActif() != null)
                existing.setActif(adherent.getActif());

            // ✔ compatible BLOB
            if (adherent.getPhotoProfil() != null)
                existing.setPhotoProfil(adherent.getPhotoProfil());

            if (adherent.getPhotoType() != null)
                existing.setPhotoType(adherent.getPhotoType());

            if (adherent.getCin() != null)
                existing.setCin(adherent.getCin());

            if (adherent.getMatricule() != null)
                existing.setMatricule(adherent.getMatricule());

            return adherentRepository.save(existing);
        }

        return null;
    }

    // ================= DELETE =================
    @Override
    public void deleteAdherent(String matricule) {
        adherentRepository.deleteById(matricule);
    }

    // ================= PROFILE JWT =================
    @Override
    public Adherent getProfileByEmail(String email) {
        return adherentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Override
    public void updateProfileByEmail(String email, UpdateProfileRequest request) {

        Adherent adherent = adherentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // ================= INFOS =================
        if(request.getNom() != null){
            adherent.setNom(request.getNom());
        }

        if(request.getPrenom() != null){
            adherent.setPrenom(request.getPrenom());
        }

        if(request.getEmail() != null){
            adherent.setEmail(request.getEmail());
        }

        if(request.getTelephone() != null){
            adherent.setTelephone(request.getTelephone());
        }

        // ================= 🔥 PHOTO (BLOB) =================
        if(request.getPhotoProfil() != null && !request.getPhotoProfil().isEmpty()){
            try {

                if(!request.getPhotoProfil().getContentType().startsWith("image/")){
                    throw new RuntimeException("Fichier invalide");
                }

                if(request.getPhotoProfil().getSize() > 2 * 1024 * 1024){
                    throw new RuntimeException("Image trop grande (max 2MB)");
                }

                adherent.setPhotoProfil(request.getPhotoProfil().getBytes());
                adherent.setPhotoType(request.getPhotoProfil().getContentType());

            } catch (Exception e){
                throw new RuntimeException("Erreur traitement image");
            }
        }

        // ================= 🔐 PASSWORD =================
        if(request.getNewPassword() != null && !request.getNewPassword().isEmpty()){

            if(request.getCurrentPassword() == null ||
                    !passwordEncoder.matches(request.getCurrentPassword(), adherent.getPassword())){

                throw new RuntimeException("Mot de passe actuel incorrect");
            }

            adherent.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        adherentRepository.save(adherent);
    }

    // ================= OLD METHODS =================
    @Override
    public void updateProfile(String matricule, UpdateProfileRequest request) {

        Adherent adherent = adherentRepository.findById(matricule).orElse(null);

        if (adherent == null) return;

        if(request.getEmail() != null){
            adherent.setEmail(request.getEmail());
        }

        if(request.getTelephone() != null){
            adherent.setTelephone(request.getTelephone());
        }

        adherentRepository.save(adherent);
    }

    @Override
    public Adherent getProfile(String matricule) {
        return adherentRepository.findById(matricule)
                .orElseThrow(() -> new RuntimeException("Adherent non trouvé"));
    }
}
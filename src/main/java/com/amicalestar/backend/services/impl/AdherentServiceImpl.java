package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.adherent.AdherentDTO;
import com.amicalestar.backend.dto.election.AdherentLiteDTO;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.exceptions.ValidationException;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.services.interfaces.AdherentService;
import com.amicalestar.backend.dto.adherent.UpdateProfileRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdherentServiceImpl implements AdherentService {

    private final AdherentRepository adherentRepository;
    private final PasswordEncoder passwordEncoder;


    // IMPORTANT pour controller image
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
                .orElseThrow(() ->
                        new RuntimeException("Utilisateur non trouvé")
                );

        // ================= EMAIL =================

        if (request.getEmail() != null &&
                !request.getEmail().isBlank()) {

            adherentRepository.findByEmail(request.getEmail())
                    .ifPresent(user -> {

                        if (!user.getMatricule().equals(adherent.getMatricule())) {

                            throw new ValidationException(
                                    Map.of(
                                            "email",
                                            "Email déjà utilisé"
                                    )
                            );
                        }
                    });

            adherent.setEmail(
                    request.getEmail().trim()
            );
        }

        // ================= NOM =================

        if (request.getNom() != null &&
                !request.getNom().isBlank()) {

            adherent.setNom(
                    request.getNom().trim()
            );
        }

        // ================= PRENOM =================

        if (request.getPrenom() != null &&
                !request.getPrenom().isBlank()) {

            adherent.setPrenom(
                    request.getPrenom().trim()
            );
        }

        // ================= TELEPHONE =================

        if (request.getTelephone() != null &&
                !request.getTelephone().isBlank()) {

            adherentRepository.findByTelephone(
                    request.getTelephone().trim()
            ).ifPresent(user -> {

                if (!user.getMatricule()
                        .equals(adherent.getMatricule())) {

                    throw new ValidationException(
                            Map.of(
                                    "telephone",
                                    "Numéro de téléphone déjà utilisé"
                            )
                    );
                }
            });

            adherent.setTelephone(
                    request.getTelephone().trim()
            );
        }

        // ================= PHOTO =================

        try {

            if ("true".equalsIgnoreCase(
                    request.getRemovePhoto()
            )) {

                adherent.setPhotoProfil(null);
                adherent.setPhotoType(null);
            }

            else if (request.getPhotoProfil() != null &&
                    !request.getPhotoProfil().isEmpty()) {

                String contentType =
                        request.getPhotoProfil().getContentType();

                if (contentType == null ||
                        !contentType.startsWith("image/")) {

                    throw new ValidationException(
                            Map.of(
                                    "photoProfil",
                                    "Fichier invalide (image uniquement)"
                            )
                    );
                }

                if (request.getPhotoProfil().getSize()
                        > 2 * 1024 * 1024) {

                    throw new ValidationException(
                            Map.of(
                                    "photoProfil",
                                    "Image trop grande (max 2MB)"
                            )
                    );
                }

                adherent.setPhotoProfil(
                        request.getPhotoProfil().getBytes()
                );

                adherent.setPhotoType(contentType);
            }

        } catch (ValidationException e) {

            throw e;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erreur traitement image"
            );
        }

        // ================= PASSWORD =================

        if (request.getNewPassword() != null &&
                !request.getNewPassword().isBlank()) {

            if (request.getCurrentPassword() == null ||
                    request.getCurrentPassword().isBlank()) {

                throw new ValidationException(
                        Map.of(
                                "currentPassword",
                                "Mot de passe actuel requis"
                        )
                );
            }

            if (!passwordEncoder.matches(
                    request.getCurrentPassword(),
                    adherent.getPassword()
            )) {

                throw new ValidationException(
                        Map.of(
                                "currentPassword",
                                "Mot de passe actuel incorrect"
                        )
                );
            }

            adherent.setPassword(
                    passwordEncoder.encode(
                            request.getNewPassword()
                    )
            );
        }

        // ================= SAVE =================

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

    @Override
    public AdherentDTO getProfileDTOByEmail(String email) {

        Adherent a = adherentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return new AdherentDTO(
                a.getMatricule(),
                a.getNom(),
                a.getPrenom(),
                a.getEmail(),
                a.getTelephone(),
                a.getCin(),
                a.getTypeAdherent().name(),
                a.getDepartement().name(),
                "http://localhost:8080/api/user/photo/" + a.getMatricule(),
                a.getPhotoProfil() != null && a.getPhotoProfil().length > 0
        );
    }

    @Override
    public List<AdherentLiteDTO> getAllLite() {

        return adherentRepository.findAll()
                .stream()
                .map(a -> {

                    AdherentLiteDTO dto =
                            new AdherentLiteDTO();

                    dto.setMatricule(
                            a.getMatricule()
                    );

                    dto.setNom(
                            a.getNom()
                    );

                    dto.setPrenom(
                            a.getPrenom()
                    );

                    dto.setDepartement(
                            a.getDepartement().name()
                    );

                    dto.setRole(
                            a.getTypeAdherent().name()
                    );

                    return dto;
                })
                .toList();
    }
}
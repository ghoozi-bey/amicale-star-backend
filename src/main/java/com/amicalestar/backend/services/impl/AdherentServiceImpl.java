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

    // Utilisé pour controller photo profil
    @Override
    public Adherent getByMatricule(String matricule) {
        return adherentRepository.findById(matricule)
                .orElseThrow(() -> new RuntimeException("Adherent non trouvé"));
    }

    // Suppression adhérent
    @Override
    public void deleteAdherent(String matricule) {

        adherentRepository.deleteById(matricule);
    }

    // Récupération profil via email JWT
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

        // Vérification unicité email
        if (request.getEmail() != null &&
                !request.getEmail().isBlank()) {

            adherentRepository.findByEmail(request.getEmail())
                    .ifPresent(user -> {

                        // Empêche duplication email
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

        // Mise à jour nom
        if (request.getNom() != null &&
                !request.getNom().isBlank()) {

            adherent.setNom(
                    request.getNom().trim()
            );
        }

        // Mise à jour prénom
        if (request.getPrenom() != null &&
                !request.getPrenom().isBlank()) {

            adherent.setPrenom(
                    request.getPrenom().trim()
            );
        }

        // Vérification unicité téléphone
        if (request.getTelephone() != null &&
                !request.getTelephone().isBlank()) {

            adherentRepository.findByTelephone(
                    request.getTelephone().trim()
            ).ifPresent(user -> {

                // Empêche duplication téléphone
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

        // Gestion image profil
        try {

            // Suppression photo profil
            if ("true".equalsIgnoreCase(
                    request.getRemovePhoto()
            )) {

                adherent.setPhotoProfil(null);
                adherent.setPhotoType(null);
            }

            // Upload nouvelle photo
            else if (request.getPhotoProfil() != null &&
                    !request.getPhotoProfil().isEmpty()) {

                String contentType =
                        request.getPhotoProfil().getContentType();

                // Vérification type image
                if (contentType == null ||
                        !contentType.startsWith("image/")) {

                    throw new ValidationException(
                            Map.of(
                                    "photoProfil",
                                    "Fichier invalide (image uniquement)"
                            )
                    );
                }

                // Limite taille image = 2MB
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

        // Changement mot de passe sécurisé
        if (request.getNewPassword() != null &&
                !request.getNewPassword().isBlank()) {

            // Vérification mot de passe actuel obligatoire
            if (request.getCurrentPassword() == null ||
                    request.getCurrentPassword().isBlank()) {

                throw new ValidationException(
                        Map.of(
                                "currentPassword",
                                "Mot de passe actuel requis"
                        )
                );
            }

            // Vérification ancien mot de passe
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

            // Encodage nouveau mot de passe
            adherent.setPassword(
                    passwordEncoder.encode(
                            request.getNewPassword()
                    )
            );
        }

        // Sauvegarde finale profil
        adherentRepository.save(adherent);
    }

    // Ancienne méthode update profil
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

    // Récupération profil par matricule
    @Override
    public Adherent getProfile(String matricule) {
        return adherentRepository.findById(matricule)
                .orElseThrow(() -> new RuntimeException("Adherent non trouvé"));
    }

    // Conversion profil vers DTO
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

                // URL endpoint image profil
                "http://localhost:8080/api/user/photo/" + a.getMatricule(),

                // Vérifie existence photo
                a.getPhotoProfil() != null && a.getPhotoProfil().length > 0
        );
    }

    // Liste simplifiée des adhérents
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
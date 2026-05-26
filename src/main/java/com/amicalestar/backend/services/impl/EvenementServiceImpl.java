package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.entities.evenement.Evenement;
import com.amicalestar.backend.enums.StatutEvenement;
import com.amicalestar.backend.repositories.evenement.EvenementRepository;
import com.amicalestar.backend.services.interfaces.EvenementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvenementServiceImpl
        implements EvenementService {

    private final EvenementRepository evenementRepository;

    // Création événement
    @Override
    public Evenement createEvenement(
            Evenement evenement
    ) {

        // Debug remises enfants
        System.out.println(
                "REM12 ACTIVE = "
                        + evenement.getRemiseEnfant12Active()
        );

        System.out.println(
                "REM12 % = "
                        + evenement.getRemiseEnfant12Pourcentage()
        );

        // Statut actif par défaut
        evenement.setStatut(
                StatutEvenement.ACTIF
        );

        System.out.println(
                "CREATE EVENEMENT EXECUTE"
        );

        // Gestion automatique international
        if (
                evenement.getTypeEvenement() != null
                        &&
                        evenement.getTypeEvenement().getId() != null
        ) {

            Long typeId =
                    evenement.getTypeEvenement().getId();

            // OMRA / HAJJ toujours international
            if (typeId == 1) {

                evenement.setIsInternational(
                        true
                );
            }

            // Convention jamais internationale
            else if (typeId == 3) {

                evenement.setIsInternational(
                        false
                );
            }

            // Voyage : false par défaut
            else if (typeId == 2) {

                if (
                        evenement.getIsInternational()
                                == null
                ) {

                    evenement.setIsInternational(
                            false
                    );
                }
            }
        }

        // Ajout image par défaut
        if (
                evenement.getPhoto() == null
                        ||
                        evenement.getPhoto().length == 0
        ) {

            String type = "";

            if (
                    evenement.getTypeEvenement() != null
                            &&
                            evenement.getTypeEvenement().getNom() != null
            ) {

                type =
                        evenement.getTypeEvenement()
                                .getNom()
                                .toUpperCase();
            }

            try {

                InputStream is;

                // Image OMRA / HAJJ
                if (
                        type.contains("OMRA")
                                || type.contains("HAJJ")
                ) {

                    is =
                            new ClassPathResource(
                                    "static/default/HAJJetOMRA.jpg"
                            ).getInputStream();
                }

                // Image voyage
                else if (
                        type.contains("VOYAGE")
                ) {

                    is =
                            new ClassPathResource(
                                    "static/default/voyage.jpg"
                            ).getInputStream();
                }

                // Image convention par défaut
                else {

                    is =
                            new ClassPathResource(
                                    "static/default/convention.png"
                            ).getInputStream();
                }

                evenement.setPhoto(
                        is.readAllBytes()
                );

                evenement.setPhotoType(
                        "image/jpeg"
                );

            } catch (IOException e) {

                throw new RuntimeException(
                        "Erreur image par défaut",
                        e
                );
            }
        }

        return evenementRepository.save(
                evenement
        );
    }

    // Liste tous les événements
    @Override
    public List<Evenement> getAllEvenements() {

        return evenementRepository.findAll();
    }

    // Recherche événement par ID
    @Override
    public Evenement getEvenementById(Long id) {

        return evenementRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Evenement non trouvé"
                        )
                );
    }

    // Événements auxquels participe un utilisateur
    @Override
    public List<Evenement> getMesEvenements(
            String matricule
    ) {

        return evenementRepository
                .findEventsWhereUserParticipates(
                        matricule
                );
    }

    // Inscriptions utilisateur
    @Override
    public List<Evenement> getMesInscriptions(
            Long matricule
    ) {

        return evenementRepository
                .findEvenementsByAdherentInscrit(
                        matricule
                );
    }

    // Événements créés par utilisateur
    @Override
    public List<Evenement> getEvenementsCrees(
            String matricule
    ) {

        return evenementRepository
                .findByAdherent_Matricule(
                        matricule
                );
    }

    // Liste événements actifs
    @Override
    public List<Evenement> getEvenementsActifs() {

        return evenementRepository
                .findByStatutNot(
                        StatutEvenement.ARCHIVE
                );
    }

    // Archivage événement
    @Override
    public Evenement archiverEvenement(
            Long id
    ) {

        Evenement event =
                evenementRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Evenement non trouvé"
                                )
                        );

        event.setStatut(
                StatutEvenement.ARCHIVE
        );

        return evenementRepository.save(event);
    }

    // Mise à jour événement
    @Override
    public Evenement updateEvenement(
            Long id,
            Evenement evenement
    ) {

        Evenement existing =
                evenementRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Evenement non trouvé"
                                )
                        );

        // Mise à jour prix
        if (
                evenement.getPrix() != null
        ) {

            existing.setPrix(
                    evenement.getPrix()
            );
        }

        // Mise à jour titre
        if (
                evenement.getTitre() != null
        ) {

            existing.setTitre(
                    evenement.getTitre()
            );
        }

        // Mise à jour image
        if (
                evenement.getPhoto() != null
                        &&
                        evenement.getPhoto().length > 0
        ) {

            existing.setPhoto(
                    evenement.getPhoto()
            );
        }

        // Mise à jour type image
        if (
                evenement.getPhotoType() != null
        ) {

            existing.setPhotoType(
                    evenement.getPhotoType()
            );
        }

        return evenementRepository.save(
                existing
        );
    }

    // Suppression événement
    @Override
    public void deleteEvenement(Long id) {

        Evenement event =
                evenementRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Evenement non trouvé"
                                )
                        );

        // Empêche suppression si inscriptions existantes
        if (
                event.getInscriptions() != null
                        &&
                        !event.getInscriptions().isEmpty()
        ) {

            throw new RuntimeException(
                    "Impossible de supprimer : il y a des inscriptions"
            );
        }

        evenementRepository.delete(event);
    }
}
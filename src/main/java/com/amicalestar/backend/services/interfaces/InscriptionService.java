package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.evenement.*;
import com.amicalestar.backend.entities.evenement.Inscription;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InscriptionService {

    // === Inscription simple à un événement ===
    Inscription inscrire(
            String matricule,
            Long eventId
    );

    // === Création complète d’une inscription ===
    void createInscription(
            InscriptionRequest request,
            MultipartFile adherentFile,
            MultipartFile conjointFile,
            List<MultipartFile> enfantsFiles
    );

    // === Liste des inscriptions d’un adhérent ===
    List<InscriptionDTO> getInscriptionsAdherent(String email);

    // === Détails sécurisés d’une inscription ===
    InscriptionDetailsDTO getByIdSecure(
            Long id,
            String email
    );

    // === Détails complets d’une inscription ===
    InscriptionFullDTO getFullDetails(Long id);

    // === Mise à jour du statut d’une inscription ===
    void updateStatut(
            Long id,
            String statut
    );

    // === Upload du justificatif de paiement ===
    void uploadJustificatif(
            Long id,
            MultipartFile file
    );

    // === Calcul détaillé de la facture ===
    FactureDTO calculerFactureDetaillee(Inscription inscription);

    // === Liste paginée des inscriptions d’un événement ===
    Page<InscriptionListDTO> getInscriptionsByEvent(
            Long eventId,
            int page,
            int size
    );
}
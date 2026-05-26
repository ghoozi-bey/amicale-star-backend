package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.election.*;
import com.amicalestar.backend.entities.election.Election;

import java.util.List;

public interface ElectionService {

    // === Création d’une élection ===
    ElectionResponseDTO create(
            CreateElectionRequest request,
            String email
    );

    // === Liste des élections ===
    List<ElectionResponseDTO> getAll();

    // === Recherche d’une élection par id ===
    ElectionResponseDTO getById(Long id);

    // === Mise à jour d’une élection ===
    ElectionResponseDTO update(
            Long id,
            CreateElectionRequest request
    );

    // === Suppression d’une élection ===
    void delete(Long id);

    // === Mise à jour automatique du statut ===
    void updateStatut(Election e);

    // === Publication d’une élection ===
    void publish(Long id);

    // === Annulation de publication ===
    void unpublish(Long id);

    // === Rejet d’une élection ===
    void reject(Long id);

    // === Liste des adhérents éligibles ===
    List<AdherentLiteDTO> getEligibleAdherents(Long electionId);

    // === Liste des élections actives ===
    List<ElectionResponseDTO> getActiveElections();

    // === Recherche d’une élection active ===
    ElectionResponseDTO getActiveElectionById(Long id);

    // === Statistiques d’une élection ===
    List<ElectionStatsDTO> getStats(Long electionId);

    // === Attribution des rôles après élection ===
    void attribuerRoles(
            Long electionId,
            List<AttribuerRoleDTO> request
    );

    // === Liste des gagnants d’une élection ===
    List<ElectionWinnerDTO> getElectionWinners(Long electionId);

}
package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.election.*;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.election.Candidat;
import com.amicalestar.backend.entities.election.Election;
import com.amicalestar.backend.entities.evenement.TypeEvenement;
import com.amicalestar.backend.enums.StatutElection;
import com.amicalestar.backend.enums.TypeAdherent;
import com.amicalestar.backend.exceptions.ValidationException;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.repositories.election.CandidatRepository;
import com.amicalestar.backend.repositories.election.ElectionRepository;
import com.amicalestar.backend.repositories.election.VoteRepository;
import com.amicalestar.backend.repositories.evenement.TypeEvenementRepository;
import com.amicalestar.backend.services.interfaces.ElectionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionServiceImpl implements ElectionService {

    private final ElectionRepository electionRepository;
    private final AdherentRepository adherentRepository;
    private final CandidatRepository candidatRepository;
    private final VoteRepository voteRepository;

    // Gestion types événements des membres élus
    private final TypeEvenementRepository typeEvenementRepository;

    // Création élection
    @Override
    public ElectionResponseDTO create(
            CreateElectionRequest request,
            String email
    ) {

        // Recherche créateur élection
        Adherent creator =
                adherentRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new ValidationException(
                                        "Utilisateur introuvable"
                                )
                        );

        // Validation dates obligatoires
        if (
                request.getDateDebut() == null
                        || request.getDateFin() == null
        ) {

            throw new ValidationException(
                    "Les dates sont obligatoires"
            );
        }

        // Vérification cohérence dates
        if (
                request.getDateFin()
                        .isBefore(request.getDateDebut())
        ) {

            throw new ValidationException(
                    "La date de fin doit être après la date début"
            );
        }

        // Vérification date future
        if (
                request.getDateDebut()
                        .isBefore(LocalDateTime.now())
        ) {

            throw new ValidationException(
                    "La date début doit être dans le futur"
            );
        }

        // Validation nombre candidats
        if (
                request.getNombreCandidats() == null
                        || request.getNombreCandidats() <= 1
        ) {

            throw new ValidationException(
                    "Le nombre de candidats doit être supérieur à 0"
            );
        }

        // Validation nombre gagnants
        if (
                request.getNombreGagnants() == null
                        || request.getNombreGagnants() <= 0
        ) {

            throw new RuntimeException(
                    "Le nombre de gagnants doit être supérieur à 0"
            );
        }

        // Nombre gagnants < nombre candidats
        if (
                request.getNombreGagnants()
                        >= request.getNombreCandidats()
        ) {

            throw new ValidationException(
                    "Le nombre de gagnants doit être inférieur au nombre de candidats"
            );
        }

        // Construction élection
        Election election = new Election();

        election.setTitle(
                request.getTitle()
        );

        election.setDescription(
                request.getDescription()
        );

        election.setDateDebut(
                request.getDateDebut()
        );

        election.setDateFin(
                request.getDateFin()
        );

        election.setNombreCandidats(
                request.getNombreCandidats()
        );

        election.setNombreGagnants(
                request.getNombreGagnants()
        );

        election.setCreatedBy(
                creator
        );

        // Ajout candidats initiaux
        if (
                request.getCandidats() != null
        ) {

            // Vérification doublons candidats
            Set<String> unique =
                    new HashSet<>(
                            request.getCandidats()
                    );

            if (
                    unique.size()
                            != request.getCandidats().size()
            ) {

                throw new ValidationException(
                        "Candidats dupliqués"
                );
            }

            // Vérification limite candidats
            if (
                    request.getCandidats().size()
                            > request.getNombreCandidats()
            ) {

                throw new ValidationException(
                        "Le nombre de candidats dépasse la limite autorisée"
                );
            }

            for (
                    String matricule :
                    request.getCandidats()
            ) {

                Adherent adherent =
                        adherentRepository
                                .findById(matricule)
                                .orElseThrow(() ->
                                        new ValidationException(
                                                "Adhérent introuvable"
                                        )
                                );

                // Interdiction responsable élection
                if (
                        adherent.getTypeAdherent()
                                == TypeAdherent.RESPONSABLE_ELECTION
                ) {

                    throw new ValidationException(
                            "Un responsable élection ne peut pas être candidat"
                    );
                }

                Candidat candidat =
                        new Candidat();

                candidat.setElection(election);

                candidat.setAdherent(adherent);

                election.getCandidats()
                        .add(candidat);
            }
        }

        election = electionRepository.save(election);

        return mapToDTO(election);
    }

    // Liste toutes les élections
    @Override
    public List<ElectionResponseDTO> getAll() {

        List<Election> elections =
                electionRepository.findAll();

        // Mise à jour automatique statuts
        elections.forEach(this::updateStatut);

        electionRepository.saveAll(elections);

        return elections.stream()
                .map(this::mapToDTO)
                .toList();
    }

    // Recherche élection par id
    @Override
    public ElectionResponseDTO getById(Long id) {

        Election election =
                electionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Election introuvable"
                                )
                        );

        updateStatut(election);

        electionRepository.save(election);

        return mapToDTO(election);
    }

    // Mise à jour élection
    @Override
    public ElectionResponseDTO update(
            Long id,
            CreateElectionRequest request
    ) {

        Election election =
                electionRepository.findById(id)
                        .orElseThrow(() ->
                                new ValidationException(
                                        "Election introuvable"
                                )
                        );

        // Synchronisation statut
        updateStatut(election);

        // Modification autorisée uniquement brouillon
        if (
                election.getStatut()
                        != StatutElection.BROUILLON
        ) {

            throw new ValidationException(
                    "Seules les élections brouillon peuvent être modifiées"
            );
        }

        // Validation dates
        if (
                request.getDateDebut() == null
                        || request.getDateFin() == null
        ) {

            throw new ValidationException(
                    "Les dates sont obligatoires"
            );
        }

        if (
                request.getDateFin()
                        .isBefore(request.getDateDebut())
        ) {

            throw new ValidationException(
                    "La date de fin doit être après la date début"
            );
        }

        if (
                request.getDateDebut()
                        .isBefore(LocalDateTime.now())
        ) {

            throw new ValidationException(
                    "La date début doit être dans le futur"
            );
        }

        // Validation candidats / gagnants
        if (
                request.getNombreCandidats() == null
                        || request.getNombreCandidats() <= 0
        ) {

            throw new ValidationException(
                    "Le nombre de candidats doit être supérieur à 0"
            );
        }

        if (
                request.getNombreGagnants() == null
                        || request.getNombreGagnants() <= 0
        ) {

            throw new ValidationException(
                    "Le nombre de gagnants doit être supérieur à 0"
            );
        }

        if (
                request.getNombreGagnants()
                        >= request.getNombreCandidats()
        ) {

            throw new ValidationException(
                    "Le nombre de gagnants doit être inférieur au nombre de candidats"
            );
        }

        // Mise à jour informations principales
        election.setTitle(request.getTitle());

        election.setDescription(
                request.getDescription()
        );

        election.setDateDebut(
                request.getDateDebut()
        );

        election.setDateFin(
                request.getDateFin()
        );

        election.setNombreCandidats(
                request.getNombreCandidats()
        );

        election.setNombreGagnants(
                request.getNombreGagnants()
        );

        // Réinitialisation candidats
        candidatRepository.deleteAll(
                election.getCandidats()
        );

        election.getCandidats().clear();

        candidatRepository.flush();

        // Réajout nouveaux candidats
        if (
                request.getCandidats() != null
        ) {

            Set<String> unique =
                    new HashSet<>(
                            request.getCandidats()
                    );

            if (
                    unique.size()
                            != request.getCandidats().size()
            ) {

                throw new ValidationException(
                        "Candidats dupliqués"
                );
            }

            if (
                    request.getCandidats().size()
                            > request.getNombreCandidats()
            ) {

                throw new ValidationException(
                        "Le nombre de candidats dépasse la limite autorisée"
                );
            }

            for (
                    String matricule :
                    request.getCandidats()
            ) {

                Adherent adherent =
                        adherentRepository
                                .findById(matricule)
                                .orElseThrow(() ->
                                        new ValidationException(
                                                "Adhérent introuvable"
                                        )
                                );

                if (
                        adherent.getTypeAdherent()
                                == TypeAdherent.RESPONSABLE_ELECTION
                ) {

                    throw new ValidationException(
                            "Un responsable élection ne peut pas être candidat"
                    );
                }

                Candidat candidat =
                        new Candidat();

                candidat.setElection(election);

                candidat.setAdherent(adherent);

                election.getCandidats()
                        .add(candidat);
            }
        }

        election = electionRepository.save(election);

        return mapToDTO(election);
    }

    // Conversion entité Election vers DTO
    private ElectionResponseDTO mapToDTO(Election election) {

        ElectionResponseDTO dto =
                new ElectionResponseDTO();

        dto.setId(election.getId());

        dto.setTitle(election.getTitle());

        dto.setDescription(
                election.getDescription()
        );

        dto.setDateCreation(
                election.getDateCreation()
        );

        dto.setDateDebut(
                election.getDateDebut()
        );

        dto.setDateFin(
                election.getDateFin()
        );

        dto.setNombreCandidats(
                election.getNombreCandidats()
        );

        dto.setNombreGagnants(
                election.getNombreGagnants()
        );

        dto.setStatut(
                election.getStatut()
        );

        // Informations créateur
        if (election.getCreatedBy() != null) {

            dto.setCreatedByNom(
                    election.getCreatedBy().getNom()
            );

            dto.setCreatedByPrenom(
                    election.getCreatedBy().getPrenom()
            );
        }

        // Conversion candidats vers DTO
        dto.setCandidats(

                election.getCandidats()
                        .stream()

                        .map(c -> {

                            CandidatResponseDTO dtoCandidat =
                                    new CandidatResponseDTO();

                            dtoCandidat.setId(
                                    c.getId()
                            );

                            dtoCandidat.setElectionId(
                                    election.getId()
                            );

                            dtoCandidat.setNom(
                                    c.getAdherent()
                                            .getNom()
                            );

                            dtoCandidat.setPrenom(
                                    c.getAdherent()
                                            .getPrenom()
                            );

                            dtoCandidat.setMatricule(
                                    c.getAdherent()
                                            .getMatricule()
                            );

                            dtoCandidat.setDepartement(
                                    c.getAdherent()
                                            .getDepartement()
                                            .name()
                            );

                            return dtoCandidat;
                        })

                        .toList()
        );

        return dto;
    }

    // Suppression élection
    @Override
    public void delete(Long id) {

        Election election =
                electionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Election introuvable"
                                )
                        );

        // Suppression uniquement si rejetée
        if (
                election.getStatut()
                        != StatutElection.REJETEE
        ) {

            throw new RuntimeException(
                    "Seules les élections rejetées peuvent être supprimées"
            );
        }

        electionRepository.delete(election);
    }

    // Mise à jour automatique statut élection
    @Override
    public void updateStatut(Election e) {

        // Ignore statuts finaux
        if (
                e.getStatut() == StatutElection.TERMINEE
                        || e.getStatut() == StatutElection.REJETEE
                        || e.getStatut() == StatutElection.FINALISEE
        ) {

            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // Brouillon expiré automatiquement
        if (
                e.getStatut() == StatutElection.BROUILLON
                        && now.isAfter(e.getDateDebut())
        ) {

            e.setStatut(
                    StatutElection.REJETEE
            );

            return;
        }

        // Activation automatique élection
        if (
                e.getStatut() == StatutElection.PUBLIEE
                        &&
                        (
                                now.isEqual(e.getDateDebut())
                                        || now.isAfter(
                                        e.getDateDebut()
                                )
                        )
                        &&
                        now.isBefore(e.getDateFin())
        ) {

            e.setStatut(
                    StatutElection.ACTIF
            );

            return;
        }

        // Fin automatique élection
        if (
                e.getStatut() == StatutElection.ACTIF
                        &&
                        now.isAfter(e.getDateFin())
        ) {

            e.setStatut(
                    StatutElection.TERMINEE
            );
        }
    }

    // Publication élection
    @Override
    public void publish(Long id) {

        Election election =
                electionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Election introuvable"
                                )
                        );

        if (
                election.getStatut()
                        != StatutElection.BROUILLON
        ) {

            throw new RuntimeException(
                    "Publication impossible"
            );
        }

        election.setStatut(
                StatutElection.PUBLIEE
        );

        electionRepository.save(election);
    }

    // Annulation publication
    @Override
    public void unpublish(Long id) {

        Election election =
                electionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Election introuvable"
                                )
                        );

        if (
                election.getStatut()
                        != StatutElection.PUBLIEE
        ) {

            throw new RuntimeException(
                    "Annulation impossible"
            );
        }

        election.setStatut(
                StatutElection.BROUILLON
        );

        electionRepository.save(election);
    }

    // Rejet élection
    @Override
    public void reject(Long id) {

        Election election =
                electionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Election introuvable"
                                )
                        );

        if (
                election.getStatut()
                        != StatutElection.BROUILLON
        ) {

            throw new RuntimeException(
                    "Seules les élections brouillon peuvent être rejetées"
            );
        }

        election.setStatut(
                StatutElection.REJETEE
        );

        electionRepository.save(election);
    }

    // Liste adhérents éligibles candidature
    @Override
    public List<AdherentLiteDTO> getEligibleAdherents(
            Long electionId
    ) {

        Election election =
                electionRepository.findById(electionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Election introuvable"
                                )
                        );

        // Liste matricules déjà candidats
        Set<String> candidatMatricules =
                election.getCandidats()
                        .stream()
                        .map(c ->
                                c.getAdherent()
                                        .getMatricule()
                        )
                        .collect(Collectors.toSet());

        return adherentRepository.findAll()
                .stream()

                // Exclusion candidats existants
                .filter(a ->
                        !candidatMatricules.contains(
                                a.getMatricule()
                        )
                )

                // Exclusion responsables élections
                .filter(a ->
                        a.getTypeAdherent()
                                != TypeAdherent.RESPONSABLE_ELECTION
                )

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
                            a.getDepartement()
                                    .name()
                    );

                    dto.setRole(
                            a.getTypeAdherent()
                                    .name()
                    );

                    return dto;
                })

                .toList();
    }

    // Liste élections actives
    @Override
    public List<ElectionResponseDTO> getActiveElections() {

        List<Election> elections =
                electionRepository.findAll();

        elections.forEach(this::updateStatut);

        electionRepository.saveAll(elections);

        return elections.stream()

                .filter(e ->
                        e.getStatut()
                                == StatutElection.ACTIF
                )

                .map(this::mapToDTO)

                .toList();
    }

    // Recherche élection active
    @Override
    public ElectionResponseDTO getActiveElectionById(Long id) {

        Election election =
                electionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Election introuvable"
                                ));

        updateStatut(election);

        electionRepository.save(election);

        if (
                election.getStatut()
                        != StatutElection.ACTIF
        ) {

            throw new RuntimeException(
                    "Election inactive"
            );
        }

        return mapToDTO(election);
    }

    // Statistiques votes candidats
    @Override
    public List<ElectionStatsDTO> getStats(Long electionId) {

        Election election =
                electionRepository.findById(electionId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Election introuvable"
                                ));

        // Autorisation statistiques
        if (
                election.getStatut()
                        != StatutElection.ACTIF
                        &&
                        election.getStatut()
                                != StatutElection.TERMINEE
                        &&
                        election.getStatut()
                                != StatutElection.FINALISEE
        ) {

            throw new RuntimeException(
                    "Les statistiques ne sont pas disponibles pour cette élection"
            );
        }

        List<ElectionStatsDTO> stats =
                new ArrayList<>();

        for (Candidat candidat : election.getCandidats()) {

            ElectionStatsDTO dto =
                    new ElectionStatsDTO();

            dto.setCandidatId(
                    candidat.getId()
            );

            dto.setNom(
                    candidat.getAdherent().getNom()
            );

            dto.setPrenom(
                    candidat.getAdherent().getPrenom()
            );

            dto.setDepartement(
                    candidat.getAdherent()
                            .getDepartement()
                            .name()
            );

            // Nombre votes candidat
            dto.setVotes(
                    voteRepository.countVotesByCandidatId(
                            candidat.getId()
                    )
            );

            stats.add(dto);
        }

        // Tri décroissant votes
        stats.sort((a, b) ->
                Long.compare(
                        b.getVotes(),
                        a.getVotes()
                ));

        return stats;
    }

    // Liste gagnants élection
    @Override
    public List<ElectionWinnerDTO>
    getElectionWinners(Long electionId) {

        Election election =
                electionRepository.findById(
                        electionId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Election introuvable"
                        )
                );

        // Vérification fin élection
        if (
                election.getStatut()
                        != StatutElection.TERMINEE
                        &&
                        election.getStatut()
                                != StatutElection.FINALISEE
        ) {

            throw new ValidationException(
                    "Les gagnants ne sont pas disponibles avant la cloture de l'election"
            );
        }

        List<Candidat> candidats =
                new ArrayList<>(
                        election.getCandidats()
                );

        // Tri candidats selon votes
        candidats.sort((a, b) -> {

            Long votesA =
                    voteRepository.countVotesByCandidatId(
                            a.getId()
                    );

            Long votesB =
                    voteRepository.countVotesByCandidatId(
                            b.getId()
                    );

            int compareVotes =
                    Long.compare(
                            votesB,
                            votesA
                    );

            // Classement principal votes
            if (compareVotes != 0) {

                return compareVotes;
            }

            // Égalité → ancienneté adhérent
            return a.getAdherent()
                    .getDateinscription()
                    .compareTo(
                            b.getAdherent()
                                    .getDateinscription()
                    );
        });

        int winnersCount =
                election.getNombreGagnants();

        List<ElectionWinnerDTO> winners =
                new ArrayList<>();

        for (
                int i = 0;
                i < Math.min(
                        winnersCount,
                        candidats.size()
                );
                i++
        ) {

            Candidat candidat =
                    candidats.get(i);

            Long votes =
                    voteRepository.countVotesByCandidatId(
                            candidat.getId()
                    );

            ElectionWinnerDTO dto =
                    new ElectionWinnerDTO();

            dto.setMatricule(
                    candidat.getAdherent()
                            .getMatricule()
            );

            dto.setNom(
                    candidat.getAdherent()
                            .getNom()
            );

            dto.setPrenom(
                    candidat.getAdherent()
                            .getPrenom()
            );

            dto.setDepartement(
                    String.valueOf(
                            candidat.getAdherent()
                                    .getDepartement()
                    )
            );

            dto.setVotes(votes);

            winners.add(dto);
        }

        return winners;
    }

    // Attribution rôles membres amicale
    @Override
    @Transactional
    public void attribuerRoles(
            Long electionId,
            List<AttribuerRoleDTO> request
    ) {

        // Vérification liste vide
        if (
                request == null
                        || request.isEmpty()
        ) {

            throw new ValidationException(
                    "Aucun membre sélectionné"
            );
        }

        Election election =
                electionRepository.findById(
                        electionId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Election introuvable"
                        )
                );

        // Vérification élection terminée
        if (
                election.getStatut()
                        != StatutElection.TERMINEE
                        &&
                        election.getStatut()
                                != StatutElection.FINALISEE
        ) {

            throw new ValidationException(
                    "Election non terminée"
            );
        }

        // Récupération gagnants
        List<ElectionWinnerDTO> winners =
                getElectionWinners(
                        electionId
                );

        Set<String> winnerMatricules =
                winners.stream()
                        .map(
                                ElectionWinnerDTO
                                        ::getMatricule
                        )
                        .collect(Collectors.toSet());

        // Vérification nombre membres
        if (
                request.size()
                        != election.getNombreGagnants()
        ) {

            throw new ValidationException(
                    "Le nombre de membres attribués doit être égal au nombre de gagnants"
            );
        }

        // Vérification doublons utilisateurs
        Set<String> uniqueMatricules =
                new HashSet<>();

        for (
                AttribuerRoleDTO dto
                : request
        ) {

            if (
                    !uniqueMatricules.add(
                            dto.getMatricule()
                    )
            ) {

                throw new ValidationException(
                        "Membres dupliqués"
                );
            }
        }

        // Vérification utilisateurs gagnants
        for (
                AttribuerRoleDTO dto
                : request
        ) {

            if (
                    !winnerMatricules.contains(
                            dto.getMatricule()
                    )
            ) {

                throw new ValidationException(
                        "Utilisateur non gagnant"
                );
            }
        }

        // Réinitialisation anciens membres amicale
        List<Adherent> oldResponsables =
                adherentRepository
                        .findByTypeAdherent(
                                TypeAdherent.MEMBRE_AMICALE
                        );

        for (Adherent adherent : oldResponsables) {

            adherent.setTypeAdherent(
                    TypeAdherent.ADHERENT
            );

            adherent.setTypeEvenement(null);
        }

        // Attribution nouveaux rôles
        for (
                AttribuerRoleDTO dto
                : request
        ) {

            Adherent adherent =
                    adherentRepository
                            .findById(
                                    dto.getMatricule()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Adhérent introuvable"
                                    )
                            );

            TypeEvenement typeEvenement =
                    typeEvenementRepository
                            .findById(
                                    dto.getTypeEvenementId()
                            )
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Type événement introuvable"
                                    )
                            );

            adherent.setTypeAdherent(
                    TypeAdherent.MEMBRE_AMICALE
            );

            adherent.setTypeEvenement(
                    typeEvenement
            );
        }

        // Finalisation élection
        election.setStatut(
                StatutElection.FINALISEE
        );

        electionRepository.save(election);
    }

}
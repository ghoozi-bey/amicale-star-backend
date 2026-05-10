package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.election.AdherentLiteDTO;
import com.amicalestar.backend.dto.election.CandidatResponseDTO;
import com.amicalestar.backend.dto.election.CreateElectionRequest;
import com.amicalestar.backend.dto.election.ElectionResponseDTO;
import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.entities.election.Candidat;
import com.amicalestar.backend.entities.election.Election;
import com.amicalestar.backend.enums.StatutElection;
import com.amicalestar.backend.enums.TypeAdherent;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.repositories.election.CandidatRepository;
import com.amicalestar.backend.repositories.election.ElectionRepository;
import com.amicalestar.backend.services.interfaces.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Override
    public ElectionResponseDTO create(CreateElectionRequest request, String email) {

        Adherent creator =
                adherentRepository.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Utilisateur introuvable"
                                )
                        );

        // VALIDATION
        if(
                request.getDateDebut() == null
                        || request.getDateFin() == null
        ) {

            throw new RuntimeException(
                    "Les dates sont obligatoires"
            );
        }

        if(
                request.getDateFin()
                        .isBefore(request.getDateDebut())
        ) {

            throw new RuntimeException(
                    "La date de fin doit être après la date début"
            );
        }

        if(
                request.getDateDebut()
                        .isBefore(LocalDateTime.now())
        ) {

            throw new RuntimeException(
                    "La date début doit être dans le futur"
            );
        }

        if(
                request.getNombreCandidats() == null
                        || request.getNombreCandidats() <= 1
        ) {

            throw new RuntimeException(
                    "Le nombre de candidats doit être supérieur à 0"
            );
        }

        if(
                request.getNombreGagnants() == null
                        || request.getNombreGagnants() <= 0
        ) {

            throw new RuntimeException(
                    "Le nombre de gagnants doit être supérieur à 0"
            );
        }

        if(
                request.getNombreGagnants()
                        >= request.getNombreCandidats()
        ) {

            throw new RuntimeException(
                    "Le nombre de gagnants doit être inférieur au nombre de candidats"
            );
        }

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

        // ===== ADD CANDIDATS =====

        if(
                request.getCandidats() != null
        ) {
            Set<String> unique =
                    new HashSet<>(
                            request.getCandidats()
                    );

            if(
                    unique.size()
                            != request.getCandidats().size()
            ) {

                throw new RuntimeException(
                        "Candidats dupliqués"
                );
            }

            if(
                    request.getCandidats().size()
                            > request.getNombreCandidats()
            ) {

                throw new RuntimeException(
                        "Le nombre de candidats dépasse la limite autorisée"
                );
            }

            for(
                    String matricule :
                    request.getCandidats()
            ) {

                Adherent adherent =
                        adherentRepository
                                .findById(matricule)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Adhérent introuvable"
                                        )
                                );

                if(
                        adherent.getTypeAdherent()
                                == TypeAdherent.RESPONSABLE_ELECTION
                ) {

                    throw new RuntimeException(
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

    @Override
    public List<ElectionResponseDTO> getAll() {

        List<Election> elections =
                electionRepository.findAll();

        elections.forEach(this::updateStatut);

        electionRepository.saveAll(elections);

        return elections.stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public ElectionResponseDTO getById(Long id) {

        Election election = electionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Election introuvable")
                );

        updateStatut(election);

        electionRepository.save(election);

        return mapToDTO(election);
    }

    @Override
    public ElectionResponseDTO update(Long id, CreateElectionRequest request) {

        Election election =
                electionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Election introuvable"
                                )
                        );

        // UPDATE STATUS
        updateStatut(election);

        // ONLY BROUILLON
        if(
                election.getStatut()
                        != StatutElection.BROUILLON
        ) {

            throw new RuntimeException(
                    "Seules les élections brouillon peuvent être modifiées"
            );
        }

        // VALIDATION
        if(
                request.getDateDebut() == null
                        || request.getDateFin() == null
        ) {

            throw new RuntimeException(
                    "Les dates sont obligatoires"
            );
        }

        if(
                request.getDateFin()
                        .isBefore(request.getDateDebut())
        ) {

            throw new RuntimeException(
                    "La date de fin doit être après la date début"
            );
        }

        if(
                request.getDateDebut()
                        .isBefore(LocalDateTime.now())
        ) {

            throw new RuntimeException(
                    "La date début doit être dans le futur"
            );
        }

        if(
                request.getNombreCandidats() == null
                        || request.getNombreCandidats() <= 0
        ) {

            throw new RuntimeException(
                    "Le nombre de candidats doit être supérieur à 0"
            );
        }

        if(
                request.getNombreGagnants() == null
                        || request.getNombreGagnants() <= 0
        ) {

            throw new RuntimeException(
                    "Le nombre de gagnants doit être supérieur à 0"
            );
        }

        if(
                request.getNombreGagnants()
                        >= request.getNombreCandidats()
        ) {

            throw new RuntimeException(
                    "Le nombre de gagnants doit être inférieur au nombre de candidats"
            );
        }

        // UPDATE INFOS
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

        // ===== RESET CANDIDATS =====

        candidatRepository.deleteAll(election.getCandidats());

        election.getCandidats().clear();

        candidatRepository.flush();

        // ===== RE-ADD =====

        if(
                request.getCandidats() != null
        ) {
            Set<String> unique =
                    new HashSet<>(
                            request.getCandidats()
                    );

            if(
                    unique.size()
                            != request.getCandidats().size()
            ) {

                throw new RuntimeException(
                        "Candidats dupliqués"
                );
            }

            if(
                    request.getCandidats().size()
                            > request.getNombreCandidats()
            ) {

                throw new RuntimeException(
                        "Le nombre de candidats dépasse la limite autorisée"
                );
            }

            for(
                    String matricule :
                    request.getCandidats()
            ) {

                Adherent adherent =
                        adherentRepository
                                .findById(matricule)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Adhérent introuvable"
                                        )
                                );

                if(
                        adherent.getTypeAdherent()
                                == TypeAdherent.RESPONSABLE_ELECTION
                ) {

                    throw new RuntimeException(
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

    private ElectionResponseDTO mapToDTO(Election election) {

        ElectionResponseDTO dto = new ElectionResponseDTO();

        dto.setId(election.getId());
        dto.setTitle(election.getTitle());
        dto.setDescription(election.getDescription());

        dto.setDateCreation(election.getDateCreation());
        dto.setDateDebut(election.getDateDebut());
        dto.setDateFin(election.getDateFin());

        dto.setNombreCandidats(election.getNombreCandidats());

        dto.setNombreGagnants(election.getNombreGagnants());

        dto.setStatut(election.getStatut());

        if(election.getCreatedBy() != null) {

            dto.setCreatedByNom(
                    election.getCreatedBy().getNom()
            );

            dto.setCreatedByPrenom(
                    election.getCreatedBy().getPrenom()
            );
        }

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

                            return dtoCandidat;
                        })

                        .toList()
        );

        return dto;
    }

    @Override
    public void delete(Long id) {

        Election election = electionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Election introuvable")
                );

        if(election.getStatut() != StatutElection.REJETEE) {

            throw new RuntimeException(
                    "Seules les élections rejetées peuvent être supprimées"
            );
        }

        electionRepository.delete(election);
    }

    @Override
    public void updateStatut(Election e) {

        // 🔒 NEVER TOUCH FINAL STATES
        if (
                e.getStatut() == StatutElection.TERMINEE
                        || e.getStatut() == StatutElection.REJETEE
                        || e.getStatut() == StatutElection.FINALISEE
        ) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        // ❌ BROUILLON expired → REJETEE
        if (
                e.getStatut() == StatutElection.BROUILLON
                        && now.isAfter(e.getDateDebut())
        ) {

            e.setStatut(StatutElection.REJETEE);

            return;
        }

        // 🟢 PUBLIEE → ACTIF
        if (
                e.getStatut() == StatutElection.PUBLIEE
                        &&
                        (
                                now.isEqual(e.getDateDebut())
                                        || now.isAfter(e.getDateDebut())
                        )
                        &&
                        now.isBefore(e.getDateFin())
        ) {

            e.setStatut(StatutElection.ACTIF);

            return;
        }

        // 🔴 ACTIF → TERMINEE
        if (
                e.getStatut() == StatutElection.ACTIF
                        &&
                        now.isAfter(e.getDateFin())
        ) {

            e.setStatut(StatutElection.TERMINEE);
        }
    }

    @Override
    public void publish(Long id) {

        Election election = electionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Election introuvable")
                );

        if(election.getStatut() != StatutElection.BROUILLON) {

            throw new RuntimeException(
                    "Publication impossible"
            );
        }

        election.setStatut(StatutElection.PUBLIEE);

        electionRepository.save(election);
    }

    @Override
    public void unpublish(Long id) {

        Election election = electionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Election introuvable")
                );

        if(election.getStatut() != StatutElection.PUBLIEE) {

            throw new RuntimeException(
                    "Annulation impossible"
            );
        }

        election.setStatut(StatutElection.BROUILLON);

        electionRepository.save(election);
    }

    @Override
    public void reject(Long id) {

        Election election = electionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Election introuvable")
                );

        if(election.getStatut() != StatutElection.BROUILLON) {

            throw new RuntimeException(
                    "Seules les élections brouillon peuvent être rejetées"
            );
        }

        election.setStatut(StatutElection.REJETEE);

        electionRepository.save(election);
    }

    @Override
    public List<AdherentLiteDTO> getEligibleAdherents(Long electionId) {

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Election introuvable"
                        )
                );

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

                // exclude already candidates
                .filter(a ->
                        !candidatMatricules.contains(
                                a.getMatricule()
                        )
                )

                // exclude responsables election
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

        if (election.getStatut()
                != StatutElection.ACTIF) {

            throw new RuntimeException(
                    "Election inactive"
            );
        }

        return mapToDTO(election);
    }

}

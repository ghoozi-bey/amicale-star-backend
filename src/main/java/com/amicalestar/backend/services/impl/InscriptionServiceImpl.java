package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.*;
import com.amicalestar.backend.entities.*;
import com.amicalestar.backend.repositories.*;
import com.amicalestar.backend.services.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Base64;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InscriptionServiceImpl implements InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final AdherentRepository adherentRepository;
    private final EvenementRepository evenementRepository;
    private final ConjointRepository conjointRepository;
    private final EnfantRepository enfantRepository;
    private final PaiementRepository paiementRepository;

    @Override
    public Inscription inscrire(String matricule, Long eventId) {

        Adherent adherent = adherentRepository.findById(matricule)
                .orElseThrow(() -> new RuntimeException("Adherent non trouvé"));

        Evenement evenement = evenementRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Evenement non trouvé"));

        Inscription inscription = Inscription.builder()
                .adherent(adherent)
                .evenement(evenement)
                .build();

        return inscriptionRepository.save(inscription);
    }

    @Override
    public void createInscription(InscriptionRequest request,
                                  MultipartFile adherentFile,
                                  MultipartFile conjointFile,
                                  List<MultipartFile> enfantsFiles) {

        // =========================
        // 1. ADHERENT
        // =========================
        Adherent adherent = adherentRepository.findById(request.getMatricule())
                .orElseThrow(() -> new RuntimeException("Adherent non trouvé"));

        // =========================
        // 2. EVENEMENT
        // =========================
        Evenement evenement = evenementRepository.findById(request.getEvenementId())
                .orElseThrow(() -> new RuntimeException("Evenement non trouvé"));

        boolean isVoyage = evenement.getTypeEvenement() != null &&
                evenement.getTypeEvenement().getNom().equalsIgnoreCase("VOYAGE");

        boolean isExterne = Boolean.TRUE.equals(evenement.getIsInternational());

        // =========================
        // 3. VALIDATION PASSEPORT
        // =========================
        if (isVoyage && isExterne) {

            if (adherentFile == null || adherentFile.isEmpty()) {
                throw new RuntimeException("Passeport obligatoire ❌");
            }

            if (request.getConjoint() != null &&
                    (conjointFile == null || conjointFile.isEmpty())) {
                throw new RuntimeException("Passeport conjoint obligatoire ❌");
            }

            if (request.getEnfants() != null && !request.getEnfants().isEmpty()) {
                if (enfantsFiles == null || enfantsFiles.isEmpty()) {
                    throw new RuntimeException("Passeports enfants obligatoires ❌");
                }
            }
        }

        // =========================
        // 4. NOMBRE PERSONNES
        // =========================
        int nbPersonnes = 1;

        if (request.getConjoint() != null) nbPersonnes++;
        if (request.getEnfants() != null) nbPersonnes += request.getEnfants().size();

        // =========================
        // 5. PLACES
        // =========================
        boolean isConvention = evenement.getTypeEvenement() != null
                && evenement.getTypeEvenement().getId() == 3;

        if (!isConvention) {

            Integer nbPlaces = evenement.getNbPlaces();

            if (nbPlaces == null || nbPlaces < nbPersonnes) {
                throw new RuntimeException("Pas assez de places ❌");
            }

            evenement.setNbPlaces(nbPlaces - nbPersonnes);
            evenementRepository.save(evenement);
        }

        // =========================
        // 6. CREATE INSCRIPTION
        // =========================
        Inscription inscription = Inscription.builder()
                .adherent(adherent)
                .evenement(evenement)
                .modePaiement(request.getModePaiement())
                .statut("EN_ATTENTE")
                .statutPaiement("NON_PAYE")
                .build();

        try {
            if (adherentFile != null && !adherentFile.isEmpty()) {
                inscription.setPassport(adherentFile.getBytes());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur fichier adherent");
        }

        inscriptionRepository.save(inscription);

        // =========================
        // 7. CONJOINT
        // =========================
        if (request.getConjoint() != null) {

            ConjointDTO dto = request.getConjoint();

            Conjoint conjoint = new Conjoint();
            conjoint.setNom(dto.getNom());
            conjoint.setPrenom(dto.getPrenom());
            conjoint.setCin(dto.getCin());
            conjoint.setTelephone(dto.getTelephone());
            conjoint.setDateNaissance(dto.getDateNaissance());
            conjoint.setInscription(inscription);

            try {
                if (conjointFile != null && !conjointFile.isEmpty()) {
                    conjoint.setPassport(conjointFile.getBytes());
                }
            } catch (Exception e) {
                throw new RuntimeException("Erreur fichier conjoint");
            }

            conjointRepository.save(conjoint);
        }

        // =========================
        // 8. ENFANTS
        // =========================
        if (request.getEnfants() != null && !request.getEnfants().isEmpty()) {

            for (int i = 0; i < request.getEnfants().size(); i++) {

                EnfantDTO dto = request.getEnfants().get(i);

                Enfant enfant = new Enfant();
                enfant.setNom(dto.getNom());
                enfant.setPrenom(dto.getPrenom());
                enfant.setDateNaissance(dto.getDateNaissance());
                enfant.setInscription(inscription);

                try {
                    if (enfantsFiles != null && i < enfantsFiles.size()) {
                        MultipartFile file = enfantsFiles.get(i);
                        if (file != null && !file.isEmpty()) {
                            enfant.setPassport(file.getBytes());
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Erreur fichier enfant");
                }

                enfantRepository.save(enfant);
            }
        }

        // =========================
        // 9. CALCUL PRIX
        // =========================
        double prixBase = evenement.getPrix() != null ? evenement.getPrix() : 0;

        int nbMoins12 = 0;
        int nbMoins18 = 0;

        if (request.getEnfants() != null) {
            for (EnfantDTO enfant : request.getEnfants()) {

                int age = calculAge(enfant.getDateNaissance());

                if (age <= 12) nbMoins12++;
                else if (age <= 18) nbMoins18++;
            }
        }

        double total = prixBase * nbPersonnes;
        double remise = 0;

        if (Boolean.TRUE.equals(evenement.getRemiseEnfant12Active())) {
            remise += nbMoins12 * (prixBase * (evenement.getRemiseEnfant12Pourcentage() != null ? evenement.getRemiseEnfant12Pourcentage() : 0) / 100);
        }

        if (Boolean.TRUE.equals(evenement.getRemiseEnfant18Active())) {
            remise += nbMoins18 * (prixBase * (evenement.getRemiseEnfant18Pourcentage() != null ? evenement.getRemiseEnfant18Pourcentage() : 0) / 100);
        }

        if (Boolean.TRUE.equals(evenement.getRemiseCoupleActive()) && request.getConjoint() != null) {
            remise += prixBase * (evenement.getRemiseCouplePourcentage() != null ? evenement.getRemiseCouplePourcentage() : 0) / 100;
        }

        double prixFinal = total - remise;

        // =========================
        // 10. AVANCE + ECHEANCIER
        // =========================
        double avance = request.getAvance() != null ? request.getAvance() : 0;
        int nombreMois = request.getNombreMois() != null ? request.getNombreMois() : 1;

        if (nombreMois <= 0) nombreMois = 1;

        double reste = prixFinal - avance;
        double mensualite = reste / nombreMois;

        // 🔥 AVANCE (EN ATTENTE)
        if (avance > 0) {
            Paiement p = new Paiement();
            p.setMontant(avance);
            p.setStatut("EN_ATTENTE"); // ✅ CORRECT
            p.setDatePaiement(null);   // ✅ pas encore payé
            p.setModePaiement(request.getModePaiementAvance());
            p.setInscription(inscription);
            paiementRepository.save(p);
        }

        // 🔥 ECHEANCIER
        LocalDate dateDebut = LocalDate.parse(request.getDateDebutPaiement());

        for (int i = 1; i <= nombreMois; i++) {

            Paiement p = new Paiement();
            p.setMontant(mensualite);
            p.setStatut("EN_ATTENTE"); // ✅
            p.setDatePaiement(dateDebut.plusMonths(i));
            p.setInscription(inscription);

            paiementRepository.save(p);
        }

        // =========================
        // 11. SAVE FINAL
        // =========================
        inscription.setPrixTotal(prixFinal);
        inscription.setRemiseAppliquee(remise);
        inscription.setNbEnfantsMoins12(nbMoins12);
        inscription.setNbEnfantsMoins18(nbMoins18);
        inscription.setEstCouple(request.getConjoint() != null);
        inscription.setResteAPayer(reste);

        inscriptionRepository.save(inscription);
    }

    @Override
    @Transactional
    public List<InscriptionDTO> getInscriptionsAdherent(String email) {
        return inscriptionRepository.findDTOByEmail(email);
    }

    @Transactional
    public InscriptionDetailsDTO getById(Long id) {

        Inscription i = inscriptionRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        // =========================
        // 🔥 CALCUL NOMBRE PERSONNES
        // =========================
        int nb = 1;

        if (i.getConjoint() != null) nb++;
        if (i.getEnfants() != null) nb += i.getEnfants().size();

        // =========================
        // 🔥 CALCUL PRIX
        // =========================
        double prixUnitaire = i.getEvenement() != null ? i.getEvenement().getPrix() : 0;
        double prixTotal = nb * prixUnitaire;

        // =========================
        // 🔥 BUILD DTO
        // =========================
        return InscriptionDetailsDTO.builder()
                .id(i.getId())
                .statut(i.getStatut())

                // Adherent
                .nom(i.getAdherent() != null ? i.getAdherent().getNom() : null)
                .prenom(i.getAdherent() != null ? i.getAdherent().getPrenom() : null)
                .email(i.getAdherent() != null ? i.getAdherent().getEmail() : null)
                .telephone(i.getAdherent() != null ? i.getAdherent().getTelephone() : null)

                // Event
                .titre(i.getEvenement() != null ? i.getEvenement().getTitre() : null)
                .prix(prixTotal) // 🔥 PRIX TOTAL

                // Conjoint
                .conjointNom(
                        i.getConjoint() != null
                                ? i.getConjoint().getNom()
                                : null
                )

                // Enfants
                .enfants(
                        i.getEnfants() != null
                                ? i.getEnfants()
                                .stream()
                                .map(e -> e.getNom())
                                .toList()
                                : List.of()
                )

                .build();
    }


    @Transactional
    public InscriptionDetailsDTO getByIdSecure(Long id, String email) {

        Inscription i = inscriptionRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));

        if (!i.getAdherent().getEmail().equals(email)) {
            throw new RuntimeException("Accès refusé ❌");
        }

        return InscriptionDetailsDTO.builder()
                .id(i.getId())
                .statut(i.getStatut())
                .nom(i.getAdherent().getNom())
                .prenom(i.getAdherent().getPrenom())
                .email(i.getAdherent().getEmail())
                .telephone(i.getAdherent().getTelephone())
                .titre(i.getEvenement().getTitre())
                .prix(i.getEvenement().getPrix())
                .modePaiement(i.getModePaiement())
                .statutPaiement(i.getStatutPaiement())
                .build();
    }
    @Override
    public List<InscriptionListDTO> getInscriptionsByEvent(Long eventId) {
        return inscriptionRepository.findDTOByEventId(eventId);
    }
    private String toBase64(byte[] file) {
        if (file == null) return null;
        return Base64.getEncoder().encodeToString(file);
    }
    @Override
    public InscriptionFullDTO getFullDetails(Long id) {

        Inscription i = inscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));

        Evenement e = i.getEvenement();
        Adherent a = i.getAdherent();

        // 🔥 CALCUL PRIX TOTAL
        int nbPersonnes = 1;

        if (i.getConjoint() != null) nbPersonnes++;

        if (i.getEnfants() != null) nbPersonnes += i.getEnfants().size();

        double prixTotal = (e != null && e.getPrix() != null ? e.getPrix() : 0) * nbPersonnes;

        return InscriptionFullDTO.builder()

                // 🔹 ID
                .id(i.getId())

                // 🔹 ADHERENT
                .nom(a != null ? a.getNom() : null)
                .prenom(a != null ? a.getPrenom() : null)
                .email(a != null ? a.getEmail() : null)
                .telephone(a != null ? a.getTelephone() : null)
                .cin(a != null ? a.getCin() : null)

                // 🔹 EVENT
                .titre(e != null ? e.getTitre() : null)
                .prix(e != null ? e.getPrix() : null)
                .typeEvenement(e != null && e.getTypeEvenement() != null
                        ? e.getTypeEvenement().getNom()
                        : null)

                // 🔹 PAIEMENT (✅ STRING → PAS .name())
                .modePaiement(i.getModePaiement())
                .statutPaiement(i.getStatutPaiement())

                // 🔹 STATUT (STRING aussi)
                .statut(i.getStatut())

                // 🔹 FAMILLE
                .conjoint(mapConjoint(i.getConjoint()))
                .enfants(mapEnfants(i.getEnfants()))

                // 🔹 PASSEPORT ADHERENT (✅ BON NOM : passport)
                .passeport(toBase64(i.getPassport()))

                // 🔹 PRIX TOTAL
                .prixTotal(prixTotal)

                .build();
    }
    private ConjointFullDTO mapConjoint(Conjoint c) {

        if (c == null) return null;

        return ConjointFullDTO.builder()
                .nom(c.getNom())
                .prenom(c.getPrenom())
                .dateNaissance(c.getDateNaissance())
                .cin(c.getCin())
                .telephone(c.getTelephone())
                .passeport(
                        c.getPassport() != null
                                ? toBase64(c.getPassport())
                                : null
                )
                .build();
    }
    private List<EnfantFullDTO> mapEnfants(List<Enfant> enfants) {

        if (enfants == null) return List.of();

        return enfants.stream()
                .map(enfant -> EnfantFullDTO.builder()
                        .nom(enfant.getNom())
                        .prenom(enfant.getPrenom())
                        .dateNaissance(enfant.getDateNaissance())
                        .passeport(
                                enfant.getPassport() != null
                                        ? toBase64(enfant.getPassport())
                                        : null
                        )
                        .build())
                .toList();
    }
    @Override
    public void updateStatut(Long id, String statut) {
        Inscription inscription = inscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscription non trouvée"));

        inscription.setStatut(statut);
        // 🔥 CALCUL PRIX FINAL
        calculerPrixFinal(inscription);

        inscriptionRepository.save(inscription);

    }
    @Override
    public void uploadJustificatif(Long id, MultipartFile file) {

        try {
            Inscription inscription = inscriptionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Inscription introuvable"));

            // 🔒 Vérifier mode paiement
            if (inscription.getModePaiement() == null ||
                    !inscription.getModePaiement().equalsIgnoreCase("VIREMENT")) {
                throw new RuntimeException("Upload autorisé uniquement pour paiement par virement");
            }

            // 🔒 Vérifier statut accepté
            if (inscription.getStatut() == null ||
                    !inscription.getStatut().equalsIgnoreCase("ACCEPTEE")) {
                throw new RuntimeException("L'inscription doit être acceptée avant upload");
            }

            // 🔒 Vérifier fichier
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("Fichier vide");
            }

            // 🔒 Vérifier type PDF
            if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
                throw new RuntimeException("Seuls les fichiers PDF sont autorisés");
            }

            // 🔥 Enregistrer fichier
            inscription.setJustificatifVirement(file.getBytes());
            inscription.setJustificatifType(file.getContentType());

            inscriptionRepository.save(inscription);

        } catch (Exception e) {
            throw new RuntimeException("Erreur upload justificatif : " + e.getMessage());
        }
    }

    private double calculerPrixFinal(Inscription inscription) {

        Evenement event = inscription.getEvenement();

        double prix = event.getPrix();
        double remiseTotale = 0;

        // 🔥 ENFANT -12
        if (Boolean.TRUE.equals(event.getRemiseEnfant12Active())
                && inscription.getNbEnfantsMoins12() != null
                && inscription.getNbEnfantsMoins12() > 0) {

            remiseTotale += event.getRemiseEnfant12Pourcentage();
        }

        // 🔥 ENFANT -18
        if (Boolean.TRUE.equals(event.getRemiseEnfant18Active())
                && inscription.getNbEnfantsMoins18() != null
                && inscription.getNbEnfantsMoins18() > 0) {

            remiseTotale += event.getRemiseEnfant18Pourcentage();
        }

        // 🔥 COUPLE
        if (Boolean.TRUE.equals(event.getRemiseCoupleActive())
                && Boolean.TRUE.equals(inscription.getEstCouple())) {

            remiseTotale += event.getRemiseCouplePourcentage();
        }

        // 🔥 CALCUL FINAL
        double prixFinal = prix - (prix * remiseTotale / 100);

        // 🔥 STOCKAGE
        inscription.setRemiseAppliquee(remiseTotale);
        inscription.setPrixTotal(prixFinal);

        return prixFinal;
    }
    private int calculAge(String date) {
        if (date == null) return 0;

        java.time.LocalDate birth = java.time.LocalDate.parse(date);
        java.time.LocalDate today = java.time.LocalDate.now();

        int age = today.getYear() - birth.getYear();

        if (today.getDayOfYear() < birth.getDayOfYear()) {
            age--;
        }

        return age;
    }


}
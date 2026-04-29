package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.dto.evenement.*;
import com.amicalestar.backend.entities.*;
import com.amicalestar.backend.entities.evenement.*;
import com.amicalestar.backend.repositories.*;
import com.amicalestar.backend.repositories.evenement.*;
import com.amicalestar.backend.services.interfaces.InscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Base64;

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
        // 🔒 BLOQUER DOUBLE INSCRIPTION
        // =========================
        if (inscriptionRepository.existsByAdherentMatriculeAndEvenementId(
                adherent.getMatricule(), evenement.getId())) {

            throw new RuntimeException("Vous êtes déjà inscrit à cet événement ");
        }

        // =========================
        // 3. VALIDATION PASSEPORT
        // =========================
        if (isVoyage && isExterne) {

            if (adherentFile == null || adherentFile.isEmpty()) {
                throw new RuntimeException("Passeport obligatoire ");
            }

            if (request.getConjoint() != null &&
                    (conjointFile == null || conjointFile.isEmpty())) {
                throw new RuntimeException("Passeport conjoint obligatoire ");
            }

            if (request.getEnfants() != null && !request.getEnfants().isEmpty()) {
                if (enfantsFiles == null || enfantsFiles.isEmpty()) {
                    throw new RuntimeException("Passeports enfants obligatoires ");
                }
            }
        }

        // =========================
        // 4. CREATE INSCRIPTION
        // =========================
        Inscription inscription = Inscription.builder()
                .adherent(adherent)
                .evenement(evenement)
                .statut("EN_ATTENTE")
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
        // 5. CONJOINT
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
        // 6. ENFANTS
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
        // 🔥 7. CALCUL PRIX CORRIGÉ
        // =========================

        double prix = evenement.getPrix() != null ? evenement.getPrix() : 0;

        // 🔹 ADULTES
        int nbAdultes = request.getConjoint() != null ? 2 : 1;
        double totalAdultes = prix * nbAdultes;

        // 🔹 ENFANTS
        int nb12 = 0;
        int nb18 = 0;
        int nbPlus18 = 0;

        if (request.getEnfants() != null) {
            for (EnfantDTO enfant : request.getEnfants()) {

                int age = calculAge(enfant.getDateNaissance());

                if (age < 12) nb12++;
                else if (age < 18) nb18++;
                else nbPlus18++; // ✅ CORRECTION
            }
        }

        double totalEnfants = 0;
        double remiseEnfants = 0;

        // -12
        if (nb12 > 0) {
            double remise = prix * evenement.getRemiseEnfant12Pourcentage() / 100;
            totalEnfants += nb12 * (prix - remise);
            remiseEnfants += nb12 * remise;
        }

        // -18
        if (nb18 > 0) {
            double remise = prix * evenement.getRemiseEnfant18Pourcentage() / 100;
            totalEnfants += nb18 * (prix - remise);
            remiseEnfants += nb18 * remise;
        }

        // +18 (AUCUNE REMISE)
        if (nbPlus18 > 0) {
            totalEnfants += nbPlus18 * prix;
        }

        // 🔹 REMISE COUPLE
        double remiseCouple = 0;

        if (Boolean.TRUE.equals(evenement.getRemiseCoupleActive())
                && request.getConjoint() != null) {

            remiseCouple = totalAdultes * evenement.getRemiseCouplePourcentage() / 100;
            totalAdultes -= remiseCouple;
        }

        // 🔹 TOTAL FINAL
        double prixFinal = totalAdultes + totalEnfants;
        double remiseTotale = remiseCouple + remiseEnfants;

        // =========================
        // 8. AVANCE
        // =========================
        double avance = request.getAvance() != null ? request.getAvance() : 0;
        int nombreMois = request.getNombreMois() != null ? request.getNombreMois() : 1;

        if (nombreMois <= 0) nombreMois = 1;

        double reste = prixFinal - avance;
        double mensualite = reste / nombreMois;

        if (avance > 0) {
            Paiement p = new Paiement();
            p.setMontant(avance);
            p.setStatut("EN_ATTENTE");
            p.setModePaiement(request.getModePaiementAvance());
            p.setInscription(inscription);
            paiementRepository.save(p);
        }

        LocalDate dateDebut = LocalDate.parse(request.getDateDebutPaiement());

        for (int i = 1; i <= nombreMois; i++) {
            Paiement p = new Paiement();
            p.setMontant(mensualite);
            p.setStatut("EN_ATTENTE");
            p.setDatePaiement(dateDebut.plusMonths(i));
            p.setModePaiement(request.getModePaiementEcheance());
            p.setInscription(inscription);

            paiementRepository.save(p);
        }

        // =========================
        // 9. SAVE FINAL
        // =========================
        inscription.setPrixTotal(prixFinal);
        inscription.setRemiseAppliquee(remiseTotale);
        inscription.setNbEnfantsMoins12(nb12);
        inscription.setNbEnfantsMoins18(nb18);
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

        // 🔥 récupérer paiements
        List<PaiementDTO> paiements = i.getPaiements() != null
                ? i.getPaiements().stream().map(p -> PaiementDTO.builder()
                .id(p.getId())
                .montant(p.getMontant())
                .statut(p.getStatut())
                .modePaiement(p.getModePaiement())
                .datePaiement(
                        p.getDatePaiement() != null
                                ? LocalDate.parse(p.getDatePaiement().toString())
                                : null
                )
                .hasJustificatif(p.getJustificatifVirement() != null)
                .build()
        ).toList()
                : List.of();

        return InscriptionDetailsDTO.builder()
                .id(i.getId())
                .statut(i.getStatut())

                // 👤 Adhérent
                .nom(i.getAdherent().getNom())
                .prenom(i.getAdherent().getPrenom())
                .email(i.getAdherent().getEmail())
                .telephone(i.getAdherent().getTelephone())

                // 🎉 Event
                .titre(i.getEvenement().getTitre())
                .prix(i.getEvenement().getPrix())
                .prixTotal(i.getPrixTotal())

                // ❌ SUPPRIMÉ (ancienne logique)
                // .modePaiement(...)
                // .statutPaiement(...)

                // 👨‍👩‍👧 Famille
                .conjointNom(
                        i.getConjoint() != null
                                ? i.getConjoint().getNom()
                                : null
                )
                .enfants(
                        i.getEnfants() != null
                                ? i.getEnfants().stream()
                                .map(e -> e.getNom())
                                .toList()
                                : List.of()
                )

                // 💰 NOUVEAU
                .paiements(paiements)

                .build();
    }
    @Override
    public Page<InscriptionListDTO> getInscriptionsByEvent(Long eventId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return inscriptionRepository.findDTOByEventId(eventId, pageable);
    }
    private String toBase64(byte[] file) {
        if (file == null) return null;
        return Base64.getEncoder().encodeToString(file);
    }
    @Override
    @Transactional
    public InscriptionFullDTO getFullDetails(Long id) {

        Inscription i = inscriptionRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable"));

        Evenement e = i.getEvenement();
        Adherent a = i.getAdherent();

        // 🔥 CALCUL PRIX TOTAL
        int nbPersonnes = 1;

        if (i.getConjoint() != null) nbPersonnes++;

        if (i.getEnfants() != null) nbPersonnes += i.getEnfants().size();

        double prixTotal = (e != null && e.getPrix() != null ? e.getPrix() : 0) * nbPersonnes;

        // 🔥 NOUVEAU : récupérer les paiements
        List<PaiementDTO> paiements = i.getPaiements() != null
                ? i.getPaiements().stream().map(p -> PaiementDTO.builder()
                .id(p.getId())
                .montant(p.getMontant())
                .statut(p.getStatut())
                .modePaiement(p.getModePaiement())
                .datePaiement(
                        p.getDatePaiement() != null
                                ? LocalDate.parse(p.getDatePaiement().toString())
                                : null
                )
                .hasJustificatif(p.getJustificatifVirement() != null)
                .build()
        ).toList()
                : List.of();

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

                // 🔹 STATUT
                .statut(i.getStatut())

                // 🔹 FAMILLE
                .conjoint(mapConjoint(i.getConjoint()))
                .enfants(mapEnfants(i.getEnfants()))

                // 🔹 PASSEPORT
                .passeport(toBase64(i.getPassport()))

                // 🔹 PRIX TOTAL
                .prixTotal(prixTotal)

                // 🔥 NOUVEAU (IMPORTANT)
                .paiements(paiements)

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
    public void uploadJustificatif(Long paiementId, MultipartFile file) {

        try {
            Paiement paiement = paiementRepository.findById(paiementId)
                    .orElseThrow(() -> new RuntimeException("Paiement introuvable"));

            Inscription inscription = paiement.getInscription();

            // 🔒 Vérifier statut inscription
            if (inscription.getStatut() == null ||
                    !inscription.getStatut().equalsIgnoreCase("ACCEPTEE")) {
                throw new RuntimeException("L'inscription doit être acceptée avant upload");
            }

            // 🔒 Vérifier mode paiement (niveau Paiement maintenant)
            if (paiement.getModePaiement() == null ||
                    !paiement.getModePaiement().equalsIgnoreCase("VIREMENT")) {
                throw new RuntimeException("Upload autorisé uniquement pour paiement par virement");
            }

            // 🔒 Vérifier statut paiement
            if (!"EN_ATTENTE".equalsIgnoreCase(paiement.getStatut())) {
                throw new RuntimeException("Paiement déjà traité");
            }

            // 🔒 Vérifier fichier
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("Fichier vide");
            }

            // 🔒 Vérifier PDF
            if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
                throw new RuntimeException("Seuls les fichiers PDF sont autorisés");
            }

            // 🔥 SAUVEGARDE DANS PAIEMENT (IMPORTANT)
            paiement.setJustificatifVirement(file.getBytes());
            paiement.setJustificatifValide(false); // en attente validation admin
            paiement.setStatut("EN_VERIFICATION");

            paiementRepository.save(paiement);

        } catch (Exception e) {
            throw new RuntimeException("Erreur upload justificatif : " + e.getMessage());
        }
    }

    private double calculerPrixFinal(Inscription inscription) {

        Evenement event = inscription.getEvenement();
        double prix = event != null && event.getPrix() != null ? event.getPrix() : 0;

        // 🔥 ADULTES
        int nbAdultes = Boolean.TRUE.equals(inscription.getEstCouple()) ? 2 : 1;

        // 🔥 ENFANTS
        int nb12 = inscription.getNbEnfantsMoins12() != null ? inscription.getNbEnfantsMoins12() : 0;
        int nb18 = inscription.getNbEnfantsMoins18() != null ? inscription.getNbEnfantsMoins18() : 0;

        // 👉 si tu as une liste enfants → meilleur :
        int nbTotalEnfants = nb12 + nb18;

        // 🔥 BASE
        double totalAdultes = prix * nbAdultes;
        double totalEnfants = prix * nbTotalEnfants;

        double remiseTotale = 0;

        // =========================
        // 🔥 REMISE COUPLE
        // =========================
        if (Boolean.TRUE.equals(event.getRemiseCoupleActive())
                && Boolean.TRUE.equals(inscription.getEstCouple())) {

            double remise = totalAdultes * event.getRemiseCouplePourcentage() / 100;
            totalAdultes -= remise;

            remiseTotale += remise; // ✅ montant réel
        }

        // =========================
        // 🔥 REMISE ENFANTS -12
        // =========================
        if (Boolean.TRUE.equals(event.getRemiseEnfant12Active()) && nb12 > 0) {

            double remise = (prix * event.getRemiseEnfant12Pourcentage() / 100) * nb12;
            totalEnfants -= remise;

            remiseTotale += remise;
        }

        // =========================
        // 🔥 REMISE ENFANTS -18
        // =========================
        if (Boolean.TRUE.equals(event.getRemiseEnfant18Active()) && nb18 > 0) {

            double remise = (prix * event.getRemiseEnfant18Pourcentage() / 100) * nb18;
            totalEnfants -= remise;

            remiseTotale += remise;
        }

        // =========================
        // 🔥 TOTAL FINAL
        // =========================
        double prixFinal = totalAdultes + totalEnfants;

        // 🔥 STOCKAGE
        inscription.setRemiseAppliquee(remiseTotale); // ✅ montant correct
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
    @Override
    public FactureDTO calculerFactureDetaillee(Inscription inscription) {

        Evenement event = inscription.getEvenement();
        double prix = event.getPrix();

        FactureDTO f = new FactureDTO();

        f.prixUnitaire = prix;
        f.remiseCouple = 0;
        f.remiseEnfants = 0;

        // =========================
        // 🔥 ADULTES
        // =========================
        f.nbAdultes = Boolean.TRUE.equals(inscription.getEstCouple()) ? 2 : 1;
        double totalAdultes = prix * f.nbAdultes;

        // =========================
        // 🔥 ENFANTS (DTO SAFE 🔥)
        // =========================
        List<EnfantDTO> enfantsDTO =
                inscriptionRepository.findEnfantsDTOByInscriptionId(inscription.getId());

        int nb12 = 0;
        int nb18 = 0;

        for (EnfantDTO e : enfantsDTO) {

            if (e.getDateNaissance() == null) continue;

            try {
                LocalDate naissance = LocalDate.parse(e.getDateNaissance());
                int age = Period.between(naissance, LocalDate.now()).getYears();

                if (age < 12) nb12++;
                else if (age < 18) nb18++;

            } catch (Exception ex) {
                continue;
            }
        }

        int nbTotal = enfantsDTO.size();

        f.nbEnfantsTotal = nbTotal;
        f.nbEnfantsMoins12 = nb12;
        f.nbEnfantsMoins18 = nb18;
        f.enfants = enfantsDTO;

        double totalEnfants = prix * nbTotal;

        // =========================
        // 🔥 REMISE COUPLE
        // =========================
        if (Boolean.TRUE.equals(event.getRemiseCoupleActive())
                && Boolean.TRUE.equals(inscription.getEstCouple())) {

            double remise = totalAdultes * event.getRemiseCouplePourcentage() / 100;
            totalAdultes -= remise;
            f.remiseCouple = remise;
        }

        // =========================
        // 🔥 REMISE ENFANTS -12
        // =========================
        if (Boolean.TRUE.equals(event.getRemiseEnfant12Active()) && nb12 > 0) {

            double remise = (prix * event.getRemiseEnfant12Pourcentage() / 100) * nb12;
            totalEnfants -= remise;
            f.remiseEnfants += remise;
        }

        // =========================
        // 🔥 REMISE ENFANTS -18
        // =========================
        if (Boolean.TRUE.equals(event.getRemiseEnfant18Active()) && nb18 > 0) {

            double remise = (prix * event.getRemiseEnfant18Pourcentage() / 100) * nb18;
            totalEnfants -= remise;
            f.remiseEnfants += remise;
        }

        // =========================
        // 🔥 TOTAL FINAL
        // =========================
        f.totalAdultes = totalAdultes;
        f.totalEnfants = totalEnfants;
        f.totalFinal = totalAdultes + totalEnfants;

        return f;
    }




}
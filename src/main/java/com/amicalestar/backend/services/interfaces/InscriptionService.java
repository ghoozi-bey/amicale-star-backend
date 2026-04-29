package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.evenement.*;
import com.amicalestar.backend.entities.evenement.Inscription;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InscriptionService {

    // ✅ ancien
    Inscription inscrire(String matricule, Long eventId);

    // ✅ nouveau (AVEC PASSEPORT ADHERENT)
    void createInscription(InscriptionRequest request,
                           MultipartFile adherentFile,
                           MultipartFile conjointFile,
                           List<MultipartFile> enfantsFiles);

    // ✅ get (OPTIMISÉ DTO 🔥)
    List<InscriptionDTO> getInscriptionsAdherent(String email);
    InscriptionDetailsDTO getByIdSecure(Long id, String email);
    InscriptionFullDTO getFullDetails(Long id);
    void updateStatut(Long id, String statut);
    void uploadJustificatif(Long id, MultipartFile file);
    FactureDTO calculerFactureDetaillee(Inscription inscription);
    Page<InscriptionListDTO> getInscriptionsByEvent(Long eventId, int page, int size);
}

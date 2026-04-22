package com.amicalestar.backend.services;

import com.amicalestar.backend.dto.*;
import com.amicalestar.backend.entities.Inscription;
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
    List<InscriptionListDTO> getInscriptionsByEvent(Long eventId);
    InscriptionFullDTO getFullDetails(Long id);
    void updateStatut(Long id, String statut);
    void uploadJustificatif(Long id, MultipartFile file);

}

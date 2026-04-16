package com.amicalestar.backend.services;

import com.amicalestar.backend.dto.InscriptionRequest;
import com.amicalestar.backend.entities.Inscription;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InscriptionService {

    // ✅ ancien
    Inscription inscrire(String matricule, Long eventId);

    // ✅ nouveau (OBLIGATOIRE)
    void createInscription(InscriptionRequest request,
                           MultipartFile conjointFile,
                           List<MultipartFile> enfantsFiles);

    // ✅ get
    List<Inscription> getInscriptionsAdherent(String matricule);
}
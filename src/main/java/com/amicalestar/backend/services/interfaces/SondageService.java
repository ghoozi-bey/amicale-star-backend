package com.amicalestar.backend.services.interfaces;

import com.amicalestar.backend.dto.sondage.CreateSondageRequest;
import com.amicalestar.backend.dto.sondage.SondageResponse;
import com.amicalestar.backend.entities.sondage.Sondage;

import java.util.List;

public interface SondageService {

    Sondage createSondage(CreateSondageRequest request, String matricule);
    List<SondageResponse> getAllSondages();
    List<SondageResponse> getSondagesByCreatorEmail(String email);
    SondageResponse getSondageById(Long id);
    Sondage publierSondage(Long id);
    void updateStatut(Sondage s);
    Sondage annulerPublication(Long id);
    void rejeterSondage(Long id);
    Sondage updateSondage(Long id, CreateSondageRequest request);
    void supprimerSondage(Long id);
    List<SondageResponse> getActiveSondages();
    SondageResponse getActiveSondageById(Long id);
}
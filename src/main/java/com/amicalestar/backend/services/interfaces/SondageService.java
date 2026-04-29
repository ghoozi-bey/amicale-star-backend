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
    public Sondage publierSondage(Long id);
    public void updateStatut(Sondage s);
    public Sondage annulerPublication(Long id);
    public void rejeterSondage(Long id);
    Sondage updateSondage(Long id, CreateSondageRequest request);
    public void supprimerSondage(Long id);
    public List<SondageResponse> getActiveSondages();
    public SondageResponse getActiveSondageById(Long id);
}
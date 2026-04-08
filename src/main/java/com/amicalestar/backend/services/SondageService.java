package com.amicalestar.backend.services;

import com.amicalestar.backend.dto.CreateSondageRequest;
import com.amicalestar.backend.dto.SondageResponse;
import com.amicalestar.backend.entities.Sondage;

import java.util.List;

public interface SondageService {

    Sondage createSondage(CreateSondageRequest request, String matricule);
    List<SondageResponse> getAllSondages();
    List<SondageResponse> getSondagesByCreatorEmail(String email);
    SondageResponse getSondageById(Long id);
    public Sondage publierSondage(Long id);
    public void updateStatut(Sondage s);
    public Sondage annulerPublication(Long id);
}
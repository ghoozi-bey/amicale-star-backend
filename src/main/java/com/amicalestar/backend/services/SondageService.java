package com.amicalestar.backend.services;

import com.amicalestar.backend.dto.CreateSondageRequest;
import com.amicalestar.backend.entities.Sondage;

import java.util.List;


public interface SondageService {

    Sondage createSondage(CreateSondageRequest request);
    List<Sondage> getActiveSondages();
    Sondage publishSondage(Long id);
    List<Sondage> getAllSondages();
    Sondage getSondageById(Long id);
    Sondage updateSondage(Long id, CreateSondageRequest request);
    void deleteSondage(Long id);

}

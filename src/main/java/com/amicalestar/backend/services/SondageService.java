package com.amicalestar.backend.services;

import com.amicalestar.backend.dto.CreateSondageRequest;
import com.amicalestar.backend.entities.Sondage;


public interface SondageService {

    Sondage createSondage(CreateSondageRequest request);

}

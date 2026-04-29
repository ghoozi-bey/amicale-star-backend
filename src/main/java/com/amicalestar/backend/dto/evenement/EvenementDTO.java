package com.amicalestar.backend.dto.evenement;

import java.time.LocalDate;

public class EvenementDTO {

    public Long id;
    public String titre;
    public String description;
    public String lieu;


    public LocalDate dateDebut;
    public LocalDate dateFin;
    public String statut;

    public Double prix;
    public Integer nbPlaces;

    public String societe;
    public String agence;
    public String destination;
    public int nbInscriptions;

    public String photoUrl;

    // 🔥 AJOUT OBLIGATOIRE
    public Boolean isInternational;

    // 🔥 TRÈS IMPORTANT
    public Long typeEvenementId;
}
package com.amicalestar.backend.dto.evenement;

import java.util.List;

public class FactureDTO {

    public double prixUnitaire;

    public int nbAdultes;
    public double totalAdultes;

    public int nbEnfantsTotal;
    public int nbEnfantsMoins12;
    public int nbEnfantsMoins18;
    public double totalEnfants;

    public double remiseCouple;
    public double remiseEnfants;

    public double totalFinal;

    public List<EnfantDTO> enfants;
}
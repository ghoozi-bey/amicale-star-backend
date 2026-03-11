package com.amicalestar.backend.services.impl;

import com.amicalestar.backend.entities.Adherent;
import com.amicalestar.backend.enums.TypeAdherent;
import com.amicalestar.backend.repositories.AdherentRepository;
import com.amicalestar.backend.services.AdherentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdherentServiceImpl implements AdherentService {

    private final AdherentRepository adherentRepository;

    @Override
    public Adherent createAdherent(Adherent adherent) {

        if (adherent.getTypeAdherent() == null) {
            adherent.setTypeAdherent(TypeAdherent.ADHERENT);
        }

        return adherentRepository.save(adherent);
    }

    @Override
    public List<Adherent> getAllAdherents() {
        return adherentRepository.findAll();
    }

    @Override
    public Adherent getAdherentById(Long id) {
        return adherentRepository.findById(id).orElse(null);
    }

    @Override
    public Adherent updateAdherent(Long id, Adherent adherent) {

        Adherent existing = adherentRepository.findById(id).orElse(null);

        if (existing != null) {

            if (adherent.getNom() != null)
                existing.setNom(adherent.getNom());

            if (adherent.getPrenom() != null)
                existing.setPrenom(adherent.getPrenom());

            if (adherent.getEmail() != null)
                existing.setEmail(adherent.getEmail());

            if (adherent.getMotdepasse() != null)
                existing.setMotdepasse(adherent.getMotdepasse());

            if (adherent.getTypeAdherent() != null)
                existing.setTypeAdherent(adherent.getTypeAdherent());

            if (adherent.getDateNaissance() != null)
                existing.setDateNaissance(adherent.getDateNaissance());

            if (adherent.getTelephone() != null)
                existing.setTelephone(adherent.getTelephone());

            if (adherent.getDepartement() != null)
                existing.setDepartement(adherent.getDepartement());

            if (adherent.getActif() != null)
                existing.setActif(adherent.getActif());

            if (adherent.getPhotoProfil() != null)
                existing.setPhotoProfil(adherent.getPhotoProfil());

            if (adherent.getCin() != null)
                existing.setCin(adherent.getCin());

            if (adherent.getMatricule() != null)
                existing.setMatricule(adherent.getMatricule());

            return adherentRepository.save(existing);
        }

        return null;
    }

    @Override
    public void deleteAdherent(Long id) {
        adherentRepository.deleteById(id);
    }
}
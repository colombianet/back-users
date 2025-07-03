package com.inmohouse.backend.backend.services;

import com.inmohouse.backend.backend.entities.Propiedad;
import com.inmohouse.backend.backend.repositories.PropiedadRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropiedadService {

    @Autowired
    private PropiedadRepository propiedadRepository;

    public List<Propiedad> findByAgenteId(Long agenteId) {
        return propiedadRepository.findByAgenteId(agenteId);
    }

    public Propiedad save(Propiedad propiedad) {
        return propiedadRepository.save(propiedad);
    }
}

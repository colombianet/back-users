package com.inmohouse.backend.backend.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import com.inmohouse.backend.backend.entities.Propiedad;

public interface PropiedadRepository extends CrudRepository<Propiedad, Long> {
    List<Propiedad> findByAgenteId(Long agenteId);
}

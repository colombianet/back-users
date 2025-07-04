package com.inmohouse.backend.backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.inmohouse.backend.backend.entities.Propiedad;

public interface PropiedadRepository extends CrudRepository<Propiedad, Long> {
    List<Propiedad> findByAgenteId(Long agenteId);
    List<Propiedad> findByEstado(String estado);

    @Query("SELECT p.tipo, COUNT(p) FROM Propiedad p GROUP BY p.tipo")
    List<Object[]> contarPorTipo();

    @Query("SELECT p.agente.nombre, COUNT(p) FROM Propiedad p GROUP BY p.agente.nombre")
    List<Object[]> contarPorAgente();
}

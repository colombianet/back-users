package com.inmohouse.backend.backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.inmohouse.backend.backend.entities.Propiedad;

public interface PropiedadRepository extends CrudRepository<Propiedad, Long> {
    List<Propiedad> findByAgenteId(Long agenteId);

    List<Propiedad> findByEstado(String estado);

    @Query("SELECT p.tipo, COUNT(p) FROM Propiedad p GROUP BY p.tipo")
    List<Object[]> contarPorTipo();

    @Query("""
                SELECT p.agente.nombre, COUNT(p)
                FROM Propiedad p
                JOIN p.agente.roles r
                WHERE r.nombre = 'ROLE_AGENTE'
                GROUP BY p.agente.nombre
            """)
    List<Object[]> contarPorAgente();

    @Query("""
                SELECT u.nombre, COUNT(p)
                FROM User u
                JOIN u.roles r
                LEFT JOIN Propiedad p ON p.agente.id = u.id
                WHERE r.nombre = 'ROLE_AGENTE'
                GROUP BY u.nombre
            """)
    List<Object[]> contarPorAgenteIncluyendoSinPropiedades();

    @Query("""
                SELECT p FROM Propiedad p
                LEFT JOIN FETCH p.agente
                WHERE p.id = :id
            """)
    Optional<Propiedad> findByIdWithAgente(@Param("id") Long id);
}

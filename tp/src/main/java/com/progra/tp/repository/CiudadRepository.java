package com.progra.tp.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import com.progra.tp.model.Ciudad;

public interface CiudadRepository extends Neo4jRepository<Ciudad, Long> {
    Optional<Ciudad> findByNombre(String nombre);

    @Query("""
       MATCH (c:Ciudad)
       WHERE id(c) = $origenId
       OPTIONAL MATCH (c)-[r:CONECTADA_CON]->(d:Ciudad)
       RETURN c, collect(r) AS rutas, collect(d) AS destinos
       """)

    Optional<Ciudad> findByIdWithRutas(Long origenId);
}


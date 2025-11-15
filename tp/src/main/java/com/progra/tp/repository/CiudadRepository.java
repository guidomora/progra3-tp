package com.progra.tp.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import com.progra.tp.model.Ciudad;

public interface CiudadRepository extends Neo4jRepository<Ciudad, Long> {
    Optional<Ciudad> findByNombre(String nombre);

    @Query("MATCH p=(c:Ciudad)-[r:CONECTADA_CON*0..10]->(v) " + "WHERE id(c) = $origenId " + 
           "RETURN c")

    Optional<Ciudad> findByIdWithRutas(Long origenId);
}


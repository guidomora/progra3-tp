package com.progra.tp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import com.progra.tp.model.Ciudad;

public interface CiudadRepository extends Neo4jRepository<Ciudad, Long> {
    Optional<Ciudad> findByNombre(String nombre);

    @Query("""
    MATCH p = (c:Ciudad)-[:CONECTADA_CON*0..10]->(x:Ciudad)
    WHERE id(c) = $origenId
    RETURN p
    """)
    List<Ciudad> cargarSubgrafo(Long origenId);
}


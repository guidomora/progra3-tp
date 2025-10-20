package com.progra.tp.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.progra.tp.model.Ciudad;

public interface CiudadRepository extends Neo4jRepository<Ciudad, Long> {
    Optional<Ciudad> findByNombre(String nombre);
}

package com.progra.tp.repository;

import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.progra.tp.model.Agente;

public interface AgenteRepository extends Neo4jRepository<Agente, Long> {
    Optional<Agente> findById(Long id);
    
}

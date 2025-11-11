package com.progra.tp.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.progra.tp.model.Tarea;

public interface TareaRepository extends Neo4jRepository<Tarea, Long> {
}
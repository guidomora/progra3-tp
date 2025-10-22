package com.progra.tp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@RelationshipProperties
public class Ruta {
    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    @JsonIgnoreProperties("rutas") // <- acá se ignoran las rutas de la ciudad destino
    private Ciudad destino;

    private double distancia; //(peso)

    public Ruta (Ciudad destino, double distancia){
        this.destino=destino;
        this.distancia=distancia;
    }

    public Ruta(){}
}

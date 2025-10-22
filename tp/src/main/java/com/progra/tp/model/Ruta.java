package com.progra.tp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.progra.tp.model.dtos.RutaResponseDTO;

import lombok.Data;

@Data
@RelationshipProperties
public class Ruta {
    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    @JsonIgnoreProperties({"rutas"})  // 🔥 evita recursión al serializar
    private Ciudad destino;

    private double distancia; //(peso)

    public Ruta (Ciudad destino, double distancia){
        this.destino=destino;
        this.distancia=distancia;
    }

    public Ruta(){}

    // 🔹 Método auxiliar: convertir a DTO sin causar recursión
    public RutaResponseDTO toDTO() {
        return new RutaResponseDTO(
            id,
            destino != null ? destino.getId() : null,
            destino != null ? destino.getNombre() : null,
            distancia
        );
    }

}

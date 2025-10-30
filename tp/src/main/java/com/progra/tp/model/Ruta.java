package com.progra.tp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.progra.tp.model.dtos.RutaResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RelationshipProperties
public class Ruta {
    @Id @GeneratedValue
    private Long id;

    @TargetNode
    @JsonIgnoreProperties({"rutas"})
    private Ciudad destino;

    private double distancia;

    public Ruta() {}
    public Ruta(Ciudad destino, double distancia) {
        this.destino = destino;
        this.distancia = distancia;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Ruta)) return false;
        Ruta other = (Ruta) obj;
        return id != null && id.equals(other.getId());
    }

    public RutaResponseDTO toDTO() {
        return new RutaResponseDTO(
            id,
            destino != null ? destino.getId() : null,
            destino != null ? destino.getNombre() : null,
            distancia
        );
    }
}
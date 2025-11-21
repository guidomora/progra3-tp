package com.progra.tp.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.progra.tp.model.dtos.CiudadResponseDTO;
import com.progra.tp.model.dtos.RutaResponseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Node
@JsonIgnoreProperties({"rutas"})
public class Ciudad {
    @Id @GeneratedValue
    private Long id;

    private String nombre;

    @Relationship(type="CONECTADA_CON", direction=Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"destino"})
    private List<Ruta> rutas = new ArrayList<>();

    public Ciudad() {}
    public Ciudad(String nombre, List<Ruta> rutas){
        this.nombre = nombre;
        this.rutas = rutas;
    }

    // hashCode y equals basados solo en ID
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Ciudad)) return false;
        Ciudad other = (Ciudad) obj;
        return id != null && id.equals(other.getId());
    }

    public CiudadResponseDTO toDTO() {
        List<RutaResponseDTO> rutasDTO = new ArrayList<>();
        if (rutas != null) {
            for (Ruta r : rutas) rutasDTO.add(r.toDTO());
        }
        return new CiudadResponseDTO(id, nombre, rutasDTO);
    }
}

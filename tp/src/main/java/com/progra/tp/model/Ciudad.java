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

import lombok.Data;

@Data
@Node
@JsonIgnoreProperties({"rutas"}) //  evita expansión circular en respuestas

public class Ciudad {
    @Id
    @GeneratedValue
    private Long id;

    private String nombre;

    @Relationship(type="CONECTADA_CON", direction=Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"destino"}) //  evita recorrer recursivamente las rutas

    private List<Ruta> rutas = new ArrayList<>();

    public Ciudad (String nombre, List<Ruta> rutas){
        
        this.nombre=nombre;
        this.rutas=rutas;
    }

    public Ciudad() {}
// 🔹 Método auxiliar: convertir a DTO sin causar recursión
    public CiudadResponseDTO toDTO() {
        List<RutaResponseDTO> rutasDTO = new ArrayList<>();
        if (rutas != null) {
            for (Ruta r : rutas) {
                rutasDTO.add(r.toDTO());
            }
        }
        return new CiudadResponseDTO(id, nombre, rutasDTO);
    }
}

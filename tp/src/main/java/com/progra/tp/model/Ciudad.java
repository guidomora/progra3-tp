package com.progra.tp.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;

@Data
@Node
public class Ciudad {
    @Id
    @GeneratedValue
    private Long id;

    private String nombre;
    @Relationship(type="CONECTADA_CON", direction=Relationship.Direction.OUTGOING)
    private List<Ruta> rutas = new ArrayList<>();

    public Ciudad (String nombre, List<Ruta> rutas){
        
        this.nombre=nombre;
        this.rutas=rutas;
    }

    public Ciudad() {}

}

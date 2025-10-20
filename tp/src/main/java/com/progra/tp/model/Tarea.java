package com.progra.tp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.progra.tp.model.enums.TareaEnum;

import lombok.Data;


@Node
@Data
public class Tarea {
    @Id 
    @GeneratedValue
    private Long id;
    private String descripcion;
    private TareaEnum tipoTarea;
    private double recompensa;
    private boolean completada = false;

    @Relationship(type="DESTINO_EN")
    private Ciudad destino;

    public Tarea (String descripcion, TareaEnum tipoTarea, double recompensa, Ciudad destino){
        this.descripcion=descripcion;
        this.tipoTarea=tipoTarea;
        this.recompensa=recompensa;
        this.destino=destino;
    }
}

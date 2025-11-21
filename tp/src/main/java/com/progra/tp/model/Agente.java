package com.progra.tp.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.progra.tp.model.enums.AgenteEnum;

import lombok.Data;

@Data
@Node
public class Agente {
    @Id
    @GeneratedValue
    private Long id;
    private AgenteEnum tipo;

    @Relationship(type="UBICADO_EN")
    private Ciudad ubicacionActual;
    private double energiaDisponible;

    @Relationship(type = "REALIZA")
    private List<Tarea> tareasAsignadas = new ArrayList<>();

    public Agente ( AgenteEnum tipo, Ciudad ubicacionActual, double energiaDisponible, List<Tarea> tareasAsignadas){
        this.tipo=tipo;
        this.ubicacionActual=ubicacionActual;
        this.energiaDisponible=energiaDisponible;
        this.tareasAsignadas=tareasAsignadas;
    }

    public Agente() {}
}

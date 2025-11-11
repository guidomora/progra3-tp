package com.progra.tp.model.dtos;

import com.progra.tp.model.enums.TareaEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TareaAsignadaDTO {
    private Long tareaId;
    private String descripcion;
    private TareaEnum tipo;
    private String ciudadDestino;
    private double distanciaRecorrida;
    private double recompensa;
}
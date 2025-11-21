package com.progra.tp.model.dtos;

import com.progra.tp.model.enums.AgenteEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgenteAsignadoResponseDTO {
    private Long agenteId;
    private AgenteEnum tipo;
    private String ciudadActual;
    private String ciudadDestino;
    private Double distanciaRecorrida;
}

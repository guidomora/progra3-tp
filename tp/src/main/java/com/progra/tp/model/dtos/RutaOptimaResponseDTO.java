package com.progra.tp.model.dtos;

import java.util.List;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RutaOptimaResponseDTO {
    private List<CiudadRutaDTO> recorrido;
    private double distanciaTotal;
}
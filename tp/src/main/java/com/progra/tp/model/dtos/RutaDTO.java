package com.progra.tp.model.dtos;

import lombok.Data;

@Data
public class RutaDTO {
    private Long destinoId;
    private double distancia;
    private double peaje; // agregue el double peaje para calcular el poda
}

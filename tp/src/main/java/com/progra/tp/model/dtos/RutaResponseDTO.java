package com.progra.tp.model.dtos;

import lombok.Data;

@Data
public class RutaResponseDTO {
    private Long id;
    private Long destinoId;
    private String destinoNombre;
    private double distancia;
    private double peaje;

    public RutaResponseDTO(Long id, Long destinoId, String destinoNombre, double distancia, double peaje) {
        this.id = id;
        this.destinoId = destinoId;
        this.destinoNombre = destinoNombre;
        this.distancia = distancia;
        this.peaje = peaje;
    }
}
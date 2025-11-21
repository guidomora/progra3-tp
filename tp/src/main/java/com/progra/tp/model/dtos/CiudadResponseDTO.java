package com.progra.tp.model.dtos;

import java.util.List;

import lombok.Data;

@Data
public class CiudadResponseDTO {
    private Long id;
    private String nombre;
    private List<RutaResponseDTO> rutas;

    public CiudadResponseDTO(Long id, String nombre, List<RutaResponseDTO> rutas) {
        this.id = id;
        this.nombre = nombre;
        this.rutas = rutas;
    }
}
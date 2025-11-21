package com.progra.tp.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MSTAristaDTO {
    private String origen;
    private String destino;
    private double distancia;
}
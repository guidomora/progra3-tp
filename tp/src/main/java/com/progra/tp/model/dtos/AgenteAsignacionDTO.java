package com.progra.tp.model.dtos;

import java.util.ArrayList;
import java.util.List;

import com.progra.tp.model.enums.AgenteEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgenteAsignacionDTO {
    private Long agenteId;
    private AgenteEnum tipo;
    private String ciudadActual;
    private double energiaRestante;
    private List<TareaAsignadaDTO> tareasAsignadas = new ArrayList<>();
}
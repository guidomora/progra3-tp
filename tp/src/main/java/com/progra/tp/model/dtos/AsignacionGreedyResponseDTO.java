package com.progra.tp.model.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignacionGreedyResponseDTO {
    private List<AgenteAsignacionDTO> asignaciones;
    private List<Long> tareasNoAsignadas;
}
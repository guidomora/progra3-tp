package com.progra.tp.model.dtos;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MSTResponseDTO {
    private List<MSTAristaDTO> aristas;
    private double distanciaTotal;
}
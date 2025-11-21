package com.progra.tp.model.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentePDDTO {
    private Long agenteId;
    private List<CiudadDTO> ciudad;
    private double energiaGastada;

}

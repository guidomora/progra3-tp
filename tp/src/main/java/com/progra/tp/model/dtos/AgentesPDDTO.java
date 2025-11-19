package com.progra.tp.model.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentesPDDTO {
    private List<AgentePDDTO> resultados = new ArrayList<>();

    public void addResultado(AgentePDDTO dto){
        resultados.add(dto);
    }
}

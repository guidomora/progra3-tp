package com.progra.tp.model.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgenteListIdRequestDTO {
    private List<Long> agentesIds;
}

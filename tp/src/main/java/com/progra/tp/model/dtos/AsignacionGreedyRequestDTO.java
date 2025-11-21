package com.progra.tp.model.dtos;

import java.util.List;

import lombok.Data;

@Data
public class AsignacionGreedyRequestDTO {
    private List<Long> agentesIds;
    private List<Long> tareasIds;
}
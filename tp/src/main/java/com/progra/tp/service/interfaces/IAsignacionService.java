package com.progra.tp.service.interfaces;

import com.progra.tp.model.dtos.AsignacionGreedyRequestDTO;
import com.progra.tp.model.dtos.AsignacionGreedyResponseDTO;

public interface IAsignacionService {
    AsignacionGreedyResponseDTO asignarMisionesGreedy(AsignacionGreedyRequestDTO request);
}
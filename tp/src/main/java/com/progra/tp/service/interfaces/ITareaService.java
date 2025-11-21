package com.progra.tp.service.interfaces;

import java.util.List;

import com.progra.tp.model.Tarea;
import com.progra.tp.model.dtos.AgenteAsignacionDTO;
import com.progra.tp.model.dtos.TareaRequestDTO;

public interface ITareaService {
    List<Tarea> getAllTareas();

    Tarea getTareaById(Long id);

    Tarea crearTarea(Tarea tarea);

    AgenteAsignacionDTO asignarTareas(Long agenteId, TareaRequestDTO tareaDTO);
}
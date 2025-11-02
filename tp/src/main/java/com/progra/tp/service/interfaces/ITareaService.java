package com.progra.tp.service.interfaces;

import java.util.List;

import com.progra.tp.model.Tarea;

public interface ITareaService {
    List<Tarea> getAllTareas();

    Tarea getTareaById(Long id);

    Tarea crearTarea(Tarea tarea);
}
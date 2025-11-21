package com.progra.tp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.progra.tp.model.Tarea;
import com.progra.tp.service.interfaces.ITareaService;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private final ITareaService tareaService;

    public TareaController(ITareaService tareaService) {
        this.tareaService = tareaService;
    }

    @GetMapping
    public List<Tarea> getAllTareas() {
        return tareaService.getAllTareas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarea> getTareaById(@PathVariable Long id) {
        Tarea tarea = tareaService.getTareaById(id);
        if (tarea != null) {
            return ResponseEntity.ok(tarea);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/crearTarea")
    public ResponseEntity<?> crearTarea(@RequestBody Tarea tarea) {
        try {
            Tarea nuevaTarea = tareaService.crearTarea(tarea);
            return ResponseEntity.ok(nuevaTarea);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
package com.progra.tp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.progra.tp.model.Ciudad;
import com.progra.tp.model.Tarea;
import com.progra.tp.repository.TareaRepository;
import com.progra.tp.service.interfaces.ICiudadService;
import com.progra.tp.service.interfaces.ITareaService;

import jakarta.transaction.Transactional;

@Service
public class TareaService implements ITareaService {

    private final TareaRepository tareaRepository;
    private final ICiudadService ciudadService;

    public TareaService(TareaRepository tareaRepository, ICiudadService ciudadService) {
        this.tareaRepository = tareaRepository;
        this.ciudadService = ciudadService;
    }

    @Override
    public List<Tarea> getAllTareas() {
        return tareaRepository.findAll();
    }

    @Override
    public Tarea getTareaById(Long id) {
        return tareaRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Tarea crearTarea(Tarea tarea) {
        if (tarea == null) {
            throw new IllegalArgumentException("Los datos de la tarea son obligatorios.");
        }

        if (tarea.getDescripcion() == null || tarea.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la tarea no puede ser nula ni vacía.");
        }

        if (tarea.getTipoTarea() == null) {
            throw new IllegalArgumentException("El tipo de tarea es obligatorio.");
        }

        if (tarea.getRecompensa() < 0) {
            throw new IllegalArgumentException("La recompensa no puede ser negativa.");
        }

        Ciudad destino = resolverDestino(tarea.getDestino());
        if (destino == null) {
            throw new IllegalArgumentException("Debe especificarse un destino válido para la tarea.");
        }

        tarea.setDescripcion(tarea.getDescripcion().trim());
        tarea.setDestino(destino);
        tarea.setCompletada(false);

        return tareaRepository.save(tarea);
    }

    private Ciudad resolverDestino(Ciudad destinoEntrada) {
        if (destinoEntrada == null) {
            return null;
        }

        Ciudad destino = null;
        if (destinoEntrada.getId() != null) {
            destino = ciudadService.getCiudadById(destinoEntrada.getId());
        }

        if (destino == null && destinoEntrada.getNombre() != null && !destinoEntrada.getNombre().trim().isEmpty()) {
            String nombreNormalizado = destinoEntrada.getNombre().trim();
            destino = ciudadService.getCiudadByNombre(nombreNormalizado);
            if (destino == null) {
                Ciudad nuevaCiudad = new Ciudad();
                nuevaCiudad.setNombre(nombreNormalizado);
                destino = ciudadService.crearCiudad(nuevaCiudad);
            }
        }

        return destino;
    }
}
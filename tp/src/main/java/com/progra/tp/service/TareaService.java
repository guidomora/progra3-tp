package com.progra.tp.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.progra.tp.model.Agente;
import com.progra.tp.model.Ciudad;
import com.progra.tp.model.Tarea;
import com.progra.tp.model.dtos.AgenteAsignacionDTO;
import com.progra.tp.model.dtos.RutaOptimaResponseDTO;
import com.progra.tp.model.dtos.TareaAsignadaDTO;
import com.progra.tp.model.dtos.TareaRequestDTO;
import com.progra.tp.repository.AgenteRepository;
import com.progra.tp.repository.TareaRepository;
import com.progra.tp.service.interfaces.ICiudadService;
import com.progra.tp.service.interfaces.IRutaService;
import com.progra.tp.service.interfaces.ITareaService;

import jakarta.transaction.Transactional;

@Service
public class TareaService implements ITareaService {

    private final TareaRepository tareaRepository;
    private final ICiudadService ciudadService;
    private final AgenteRepository agenteRepository;
    private final IRutaService rutaService;
    public TareaService(TareaRepository tareaRepository, ICiudadService ciudadService, AgenteRepository agenteRepository,IRutaService rutaService) {
        this.tareaRepository = tareaRepository;
        this.ciudadService = ciudadService;
        this.agenteRepository=agenteRepository;
        this.rutaService=rutaService;
    }

    @Override
    public List<Tarea> getAllTareas() {
        return tareaRepository.findAll();
    }

    @Override
    public Tarea getTareaById(Long id) {
        return tareaRepository.findById(id).orElse(null);
    }

    // Greedy local por recompensa/distancia: O(M * (E log V) + M log M) sobre M tareas, mas claro: O(M*(V + E)*log V)
    @Override
    public AgenteAsignacionDTO asignarTareas(Long agenteId, TareaRequestDTO tareaDTO ){
        Agente agente = agenteRepository.findById(agenteId)
                .orElseThrow(() -> new IllegalArgumentException("Agente no encontrado con id: " + agenteId));
        List<Long> tareasId = tareaDTO.getIdTareas();
        List<Tarea> tareas=tareaRepository.findAllById(tareasId);
        if (tareas.isEmpty()) {
            throw new IllegalArgumentException("Una o mas tareas no existen.");
        }

        Map<Tarea,Double> distancias=new HashMap<>();
        for (Tarea t : tareas) {
            RutaOptimaResponseDTO respuestaRuta = rutaService
                    .calcularRutaMasCorta(agente.getUbicacionActual().getId(), t.getDestino().getId());
            double distancia= respuestaRuta.getDistanciaTotal();
            distancias.put(t, distancia);
        }

        // ordenar por recompensa/distancia
        List<Tarea> ordenadas = tareas.stream()
        .sorted(Comparator.comparingDouble(t -> -t.getRecompensa() / distancias.get(t)))
        .toList();
        
        List<TareaAsignadaDTO> asignadas = new ArrayList<>();
        List<Tarea> tareasAAsignar = new ArrayList<>();

        double energiaDisponible=agente.getEnergiaDisponible();
        for (Tarea t : ordenadas) {
            double costo = distancias.get(t);
            if (energiaDisponible >= costo) {
                TareaAsignadaDTO dto = t.toDto(t);
                dto.setDistanciaRecorrida(costo); 
                asignadas.add(dto);
                tareasAAsignar.add(t);
                energiaDisponible -= costo;
            }
        }
        if (tareasAAsignar.isEmpty()) {
            throw new IllegalArgumentException("El agente no tiene energia suficiente para ninguna tarea.");
        }

        agente.getTareasAsignadas().addAll(tareasAAsignar);
        agente.setEnergiaDisponible(energiaDisponible);
        agenteRepository.save(agente);

        AgenteAsignacionDTO response = new AgenteAsignacionDTO();
        response.setAgenteId(agente.getId());
        response.setTipo(agente.getTipo());
        response.setCiudadActual(agente.getUbicacionActual().getNombre());
        response.setEnergiaRestante(energiaDisponible);
        response.setTareasAsignadas(asignadas);

        return response;
    } //ESTO SERIA SI quisieras agregarle tareas optimas a un solo agente


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
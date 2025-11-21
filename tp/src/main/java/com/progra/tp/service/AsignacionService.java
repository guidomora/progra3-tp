package com.progra.tp.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.progra.tp.model.Agente;
import com.progra.tp.model.Tarea;
import com.progra.tp.model.dtos.AgenteAsignacionDTO;
import com.progra.tp.model.dtos.AsignacionGreedyRequestDTO;
import com.progra.tp.model.dtos.AsignacionGreedyResponseDTO;
import com.progra.tp.model.dtos.TareaAsignadaDTO;
import com.progra.tp.model.dtos.RutaOptimaResponseDTO;
import com.progra.tp.model.enums.AgenteEnum;
import com.progra.tp.model.enums.TareaEnum;
import com.progra.tp.repository.AgenteRepository;
import com.progra.tp.repository.TareaRepository;
import com.progra.tp.service.interfaces.IAsignacionService;
import com.progra.tp.service.interfaces.IRutaService;

import jakarta.transaction.Transactional;

@Service
public class AsignacionService implements IAsignacionService {

    private final AgenteRepository agenteRepository;
    private final TareaRepository tareaRepository;
    private final IRutaService rutaService;

    private static final Map<AgenteEnum, Set<TareaEnum>> COMPATIBILIDAD = crearTablaCompatibilidad();

    public AsignacionService(AgenteRepository agenteRepository, TareaRepository tareaRepository, IRutaService rutaService) {
        this.agenteRepository = agenteRepository;
        this.tareaRepository = tareaRepository;
        this.rutaService = rutaService;
    }

    // Greedy global de asignación: O(T * A * (E log V)) evaluando cada tarea contra cada agente con Dijkstra
    @Override
    @Transactional
    public AsignacionGreedyResponseDTO asignarMisionesGreedy(AsignacionGreedyRequestDTO request) {
        List<Agente> agentes = cargarAgentes(request.getAgentesIds());
        if (agentes.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron agentes disponibles para la asignación.");
        }

        List<Tarea> tareas = cargarTareas(request.getTareasIds());
        if (tareas.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron tareas para asignar.");
        }

        tareas.sort(Comparator.comparingDouble(Tarea::getRecompensa).reversed()
                .thenComparing(t -> t.getId() == null ? Long.MAX_VALUE : t.getId()));

        Map<Long, AgenteAsignacionDTO> asignaciones = new LinkedHashMap<>();
        List<Long> tareasNoAsignadas = new ArrayList<>();

        for (Tarea tarea : tareas) {
            Agente mejorAgente = null;
            double mejorCosto = Double.POSITIVE_INFINITY;

            for (Agente agente : agentes) {
                if (!esCompatible(agente, tarea)) {
                    continue;
                }

                double distancia = calcularDistancia(agente, tarea);
                if (Double.isInfinite(distancia) || distancia > agente.getEnergiaDisponible()) {
                    continue;
                }

                if (distancia < mejorCosto) {
                    mejorCosto = distancia;
                    mejorAgente = agente;
                }
            }

            if (mejorAgente == null) {
                tareasNoAsignadas.add(tarea.getId());
                continue;
            }

            asignarTarea(mejorAgente, tarea, mejorCosto, asignaciones);
        }

        agenteRepository.saveAll(agentes);

        return new AsignacionGreedyResponseDTO(new ArrayList<>(asignaciones.values()), tareasNoAsignadas);
    }

    private List<Agente> cargarAgentes(List<Long> agentesIds) {
        if (agentesIds == null || agentesIds.isEmpty()) {
            return agenteRepository.findAll();
        }

        List<Agente> agentes = agentesIds.stream()
                .map(id -> agenteRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Agente no encontrado con ID: " + id)))
                .collect(Collectors.toList());

        return agentes;
    }

    private List<Tarea> cargarTareas(List<Long> tareasIds) {
        if (tareasIds == null || tareasIds.isEmpty()) {
            return tareaRepository.findAll();
        }

        List<Tarea> tareas = new ArrayList<>();
        tareaRepository.findAllById(tareasIds).forEach(tareas::add);

        if (tareas.size() != tareasIds.size()) {
            Set<Long> encontrados = tareas.stream()
                    .map(Tarea::getId)
                    .collect(Collectors.toSet());

            List<Long> faltantes = tareasIds.stream()
                    .filter(id -> !encontrados.contains(id))
                    .collect(Collectors.toList());

            if (!faltantes.isEmpty()) {
                throw new IllegalArgumentException("No se encontraron tareas con IDs: " + faltantes);
            }
        }

        return tareas;
    }

    private static Map<AgenteEnum, Set<TareaEnum>> crearTablaCompatibilidad() {
        Map<AgenteEnum, Set<TareaEnum>> compatibilidad = new EnumMap<>(AgenteEnum.class);
        compatibilidad.put(AgenteEnum.REPARTIDOR, EnumSet.of(TareaEnum.ENTREGAR, TareaEnum.VISITAR));
        compatibilidad.put(AgenteEnum.RESCATISTA, EnumSet.of(TareaEnum.RESCATAR));
        compatibilidad.put(AgenteEnum.CAMION, EnumSet.of(TareaEnum.TRANSPORTAR, TareaEnum.ENTREGAR));
        compatibilidad.put(AgenteEnum.HELICOPTERO, EnumSet.of(TareaEnum.RESCATAR, TareaEnum.TRANSPORTAR));
        return compatibilidad;
    }

    private boolean esCompatible(Agente agente, Tarea tarea) {
        if (agente.getTipo() == null || tarea.getTipoTarea() == null) {
            return false;
        }
        Set<TareaEnum> tareasSoportadas = COMPATIBILIDAD.get(agente.getTipo());
        return tareasSoportadas != null && tareasSoportadas.contains(tarea.getTipoTarea());
    }

    private double calcularDistancia(Agente agente, Tarea tarea) {
        if (agente.getUbicacionActual() == null || agente.getUbicacionActual().getId() == null) {
            return Double.POSITIVE_INFINITY;
        }
        if (tarea.getDestino() == null || tarea.getDestino().getId() == null) {
            return Double.POSITIVE_INFINITY;
        }
        try {
            RutaOptimaResponseDTO ruta = rutaService.calcularRutaMasCorta(
                    agente.getUbicacionActual().getId(),
                    tarea.getDestino().getId());
            return ruta.getDistanciaTotal();
        } catch (IllegalArgumentException e) {
            return Double.POSITIVE_INFINITY;
        }
    }

    private void asignarTarea(Agente agente, Tarea tarea, double costo,
            Map<Long, AgenteAsignacionDTO> asignaciones) {
        if (agente.getTareasAsignadas() == null) {
            agente.setTareasAsignadas(new ArrayList<>());
        }
        agente.getTareasAsignadas().add(tarea);
        agente.setEnergiaDisponible(agente.getEnergiaDisponible() - costo);

        AgenteAsignacionDTO asignacionDTO = asignaciones.computeIfAbsent(agente.getId(), id -> new AgenteAsignacionDTO(
                agente.getId(),
                agente.getTipo(),
                agente.getUbicacionActual() != null ? agente.getUbicacionActual().getNombre() : null,
                agente.getEnergiaDisponible(),
                new ArrayList<>()));

        asignacionDTO.setEnergiaRestante(agente.getEnergiaDisponible());
        asignacionDTO.getTareasAsignadas().add(new TareaAsignadaDTO(
                tarea.getId(),
                tarea.getDescripcion(),
                tarea.getTipoTarea(),
                tarea.getDestino() != null ? tarea.getDestino().getNombre() : null,
                costo,
                tarea.getRecompensa()));
    }
}
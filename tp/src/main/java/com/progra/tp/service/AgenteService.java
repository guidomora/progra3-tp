package com.progra.tp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.progra.tp.model.Agente;
import com.progra.tp.model.Ciudad;
import com.progra.tp.model.dtos.AgenteAsignacionDTO;
import com.progra.tp.model.dtos.AgenteAsignadoResponseDTO;
import com.progra.tp.model.dtos.AgenteListIdRequestDTO;
import com.progra.tp.repository.AgenteRepository;
import com.progra.tp.repository.CiudadRepository;
import com.progra.tp.service.interfaces.IAgenteService;
import com.progra.tp.service.interfaces.ICiudadService;
import com.progra.tp.service.interfaces.IRutaService;

import jakarta.transaction.Transactional;

@Service
public class AgenteService implements IAgenteService{
    private final AgenteRepository agenteRepository;
    private final CiudadRepository ciudadRepository;
    private final ICiudadService ciudadService; // inyectamos la interfaz, no el service concreto
    private final IRutaService rutaService;
    public AgenteService(AgenteRepository agenteRepository, ICiudadService ciudadService,IRutaService rutaService,CiudadRepository ciudadRepository) {
        this.agenteRepository = agenteRepository;
        this.ciudadService = ciudadService;
        this.rutaService=rutaService;
        this.ciudadRepository=ciudadRepository;
    }
    public List<Agente> getAllAgentes(){
        return agenteRepository.findAll();
    }

    public Agente getAgenteById(Long id){
        return agenteRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Agente crearAgente(Agente agente) {
        Ciudad ciudad = null;

        if (agente.getUbicacionActual() != null && agente.getUbicacionActual().getId() != null) {
            ciudad = ciudadService.getCiudadById(agente.getUbicacionActual().getId());
        }

        if (ciudad == null && agente.getUbicacionActual() != null && agente.getUbicacionActual().getNombre() != null) {
            ciudad = ciudadService.getCiudadByNombre(agente.getUbicacionActual().getNombre());
        }

        if (ciudad == null && agente.getUbicacionActual() != null) {
            ciudad = ciudadService.crearCiudad(agente.getUbicacionActual());
        }

        agente.setUbicacionActual(ciudad);
        return agenteRepository.save(agente);
    }

    @Override
    @Transactional
    public AgenteAsignacionDTO actualizarAgente(Long id, AgenteAsignacionDTO datosActualizados) {
        Agente agenteExistente = agenteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agente no encontrado con id: " + id));

        if (datosActualizados.getTipo() != null) {
            agenteExistente.setTipo(datosActualizados.getTipo());
        }
        if (datosActualizados.getCiudadActual() != null) {
            Ciudad ciudad = ciudadService.getCiudadByNombre(datosActualizados.getCiudadActual());
            if (ciudad == null) {
                throw new IllegalArgumentException();
            }
            agenteExistente.setUbicacionActual(ciudad);
        }
        if (datosActualizados.getEnergiaRestante() > 0) {
            agenteExistente.setEnergiaDisponible(datosActualizados.getEnergiaRestante());
        }

        agenteExistente = agenteRepository.save(agenteExistente);

        AgenteAsignacionDTO response = new AgenteAsignacionDTO();
        response.setAgenteId(agenteExistente.getId());
        response.setTipo(agenteExistente.getTipo());
        response.setCiudadActual(
            agenteExistente.getUbicacionActual() != null 
                ? agenteExistente.getUbicacionActual().getNombre() 
                : null
        );
        response.setEnergiaRestante(agenteExistente.getEnergiaDisponible());
        response.setTareasAsignadas(datosActualizados.getTareasAsignadas()); 

        return response;
    }

    // Dijkstra: O(A log A)
    @Override
    @Transactional
    public AgenteAsignadoResponseDTO agenteMasCercano(Long ciudadDestinoId,AgenteListIdRequestDTO agentesRequest) {

        List<Long> ids = agentesRequest.getAgentesIds();

        List<Agente> agentes = agenteRepository.findAllById(ids);
        if (agentes.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron agentes con esos IDs");
        } 

        Agente elegido = agenteMasCercanoDAC(agentes, ciudadDestinoId, 0, agentes.size() - 1);

        double distancia = rutaService
                .calcularRutaMasCorta(
                        elegido.getUbicacionActual().getId(),
                        ciudadDestinoId)
                .getDistanciaTotal();

        Ciudad destino = ciudadRepository.findById(ciudadDestinoId)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad destino no existe"));

        return new AgenteAsignadoResponseDTO(
                elegido.getId(),
                elegido.getTipo(),
                elegido.getUbicacionActual().getNombre(),
                destino.getNombre(),
                distancia
        );
    }

    private Agente agenteMasCercanoDAC(List<Agente> agentes, Long ciudadDestinoId, int inicio, int fin) {//a=2 b=2 k= depende de dijkstra, por lo tanto: O(log.n.(V+E)logV)

        if (inicio == fin) {
            return agentes.get(inicio);
        }

        int medio = (inicio + fin) / 2;

        Agente izq = agenteMasCercanoDAC(agentes, ciudadDestinoId, inicio, medio);
        Agente der = agenteMasCercanoDAC(agentes, ciudadDestinoId, medio + 1, fin);

        double distIzq = rutaService.calcularRutaMasCorta(
                izq.getUbicacionActual().getId(), ciudadDestinoId
        ).getDistanciaTotal();

        double distDer = rutaService.calcularRutaMasCorta(
                der.getUbicacionActual().getId(), ciudadDestinoId
        ).getDistanciaTotal();

        if (distIzq <= distDer) {
            return izq;
        } else {
            return der;
        }
    }
}

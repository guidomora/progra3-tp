package com.progra.tp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.progra.tp.model.Agente;
import com.progra.tp.model.Ciudad;
import com.progra.tp.repository.AgenteRepository;
import com.progra.tp.service.interfaces.IAgenteService;
import com.progra.tp.service.interfaces.ICiudadService;

import jakarta.transaction.Transactional;

@Service
public class AgenteService implements IAgenteService{
    private final AgenteRepository agenteRepository;
    private final ICiudadService ciudadService; // inyectamos la interfaz, no el service concreto

    public AgenteService(AgenteRepository agenteRepository, ICiudadService ciudadService) {
        this.agenteRepository = agenteRepository;
        this.ciudadService = ciudadService;
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
    
}

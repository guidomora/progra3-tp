package com.progra.tp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.progra.tp.model.Agente;
import com.progra.tp.model.Ciudad;
import com.progra.tp.repository.AgenteRepository;
import com.progra.tp.service.interfaces.IAgenteService;
import com.progra.tp.service.interfaces.ICiudadService;

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
    public Agente crearAgente(Agente agente) {
        // TODO Auto-generated method stub
        Ciudad ciudad = ciudadService.getCiudadByNombre(agente.getUbicacionActual().getNombre());
        if (ciudad == null) {
            ciudad = ciudadService.crearCiudad(agente.getUbicacionActual());
        }
        agente.setUbicacionActual(ciudad);
        
        return agenteRepository.save(agente);
    }
    
}

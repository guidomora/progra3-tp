package com.progra.tp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.progra.tp.model.Agente;
import com.progra.tp.repository.AgenteRepository;
import com.progra.tp.service.interfaces.IAgenteService;

@Service
public class AgenteService implements IAgenteService{
    private final AgenteRepository agenteRepository;

    public AgenteService(AgenteRepository agenteRepository) {
        this.agenteRepository = agenteRepository;
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
        return agenteRepository.save(agente);
    }
    
}

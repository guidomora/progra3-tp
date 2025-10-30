package com.progra.tp.service.interfaces;

import java.util.List;

import com.progra.tp.model.Agente;

public interface IAgenteService {
    public List<Agente> getAllAgentes();
    public Agente getAgenteById(Long id);
    // public Agente getAgenteByNombre(String nombre);
    public Agente crearAgente(Agente agente);
}
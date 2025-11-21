package com.progra.tp.service.interfaces;

import java.util.List;

import com.progra.tp.model.Agente;
import com.progra.tp.model.dtos.AgenteAsignacionDTO;
import com.progra.tp.model.dtos.AgenteAsignadoResponseDTO;
import com.progra.tp.model.dtos.AgenteListIdRequestDTO;

public interface IAgenteService {
    public List<Agente> getAllAgentes();
    public Agente getAgenteById(Long id);
    // public Agente getAgenteByNombre(String nombre);
    public Agente crearAgente(Agente agente);
    public AgenteAsignacionDTO actualizarAgente(Long id, AgenteAsignacionDTO datosActualizados);
    public AgenteAsignadoResponseDTO agenteMasCercano(Long ciudadDestinoId,AgenteListIdRequestDTO agentesRequest);
}
package com.progra.tp.service.interfaces;

import java.util.List;

import com.progra.tp.model.Ciudad;
import com.progra.tp.model.dtos.AgenteListIdDTO;
import com.progra.tp.model.dtos.AgentesPDDTO;

public interface ICiudadService {

    Ciudad crearCiudad(Ciudad ciudad);

    List<Ciudad> getAllCiudades();

    Ciudad getCiudadByNombre(String nombre);

    Ciudad getCiudadById(Long id);

    Ciudad actualizarCiudad(Long id, Ciudad ciudadDetails);
    boolean eliminarCiudad(Long id);
    AgentesPDDTO cantidadCiudadesVisitadas(AgenteListIdDTO agentesIds);
}

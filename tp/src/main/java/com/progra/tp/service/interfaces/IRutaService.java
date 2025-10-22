package com.progra.tp.service.interfaces;
import java.util.List;

import com.progra.tp.model.Ciudad;
import com.progra.tp.model.Ruta;
public interface IRutaService {

    Ciudad agregarRuta(Long ciudadId, Ruta ruta);
    List<Ruta> obtenerRutasDeCiudad(Long ciudadId);
    Ciudad actualizarRuta(Long ciudadId, int rutaIndex, Ruta rutaActualizada);
    Ciudad eliminarRutaPorId(Long ciudadId, Long rutaIndex);
}

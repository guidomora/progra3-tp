package com.progra.tp.service.interfaces;
import java.util.List;

import com.progra.tp.model.Ciudad;
import com.progra.tp.model.Ruta;
import com.progra.tp.model.dtos.CiudadResponseDTO;
import com.progra.tp.model.dtos.RutaDTO;
import com.progra.tp.model.dtos.RutaOptimaResponseDTO;
import com.progra.tp.model.dtos.RutaResponseDTO;
public interface IRutaService {

    CiudadResponseDTO agregarRuta(Long ciudadId, RutaDTO ruta);
    List<RutaResponseDTO> obtenerRutasDeCiudad(Long ciudadId);
    Ciudad actualizarRuta(Long ciudadId, int rutaIndex, Ruta rutaActualizada);
    Ciudad eliminarRutaPorId(Long ciudadId, Long rutaIndex);
    RutaOptimaResponseDTO calcularRutaMasCorta(Long ciudadOrigenId, Long ciudadDestinoId);
    List<List<Ciudad>> encontrarRutasPorPresupuesto(Long origenId, Long destinoId, double presupuestoMaximo);
}

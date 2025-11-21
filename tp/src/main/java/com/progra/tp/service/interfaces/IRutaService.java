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
    Ciudad actualizarRuta(Long ciudadId, Long rutaIndex, Ruta rutaActualizada);
    Ciudad eliminarRutaPorId(Long ciudadId, Long rutaIndex);
    RutaOptimaResponseDTO calcularRutaMasCorta(Long ciudadOrigenId, Long ciudadDestinoId);
    List<List<Ciudad>> encontrarRutasPorPresupuesto(Long origenId, Long destinoId, double presupuestoMaximo); // Nuevo metodo para rutas por presupuesto
    RutaOptimaResponseDTO rutaConMenosEscalas(Long ciudadOrigenId, Long ciudadDestinoId); // Nuevo metodo para la ruta con menos escalas
    List<List<Ciudad>> encontrarRutasPorEscalas(Long origenId, Long destinoId, int maxEscalas); // Nuevo metodo para rutas por numero de escalas
}

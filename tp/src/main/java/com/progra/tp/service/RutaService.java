package com.progra.tp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.progra.tp.model.Ciudad;
import com.progra.tp.model.Ruta;
import com.progra.tp.repository.CiudadRepository;
import com.progra.tp.service.interfaces.IRutaService;

@Service
public class RutaService implements IRutaService {

    private final CiudadRepository ciudadRepository;

    public RutaService(CiudadRepository ciudadRepository) {
        this.ciudadRepository = ciudadRepository;
    }

    @Override
    public Ciudad agregarRuta(Long ciudadId, Ruta ruta) {
        Ciudad ciudad = ciudadRepository.findById(ciudadId)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad origen no encontrada"));

        Ciudad destinoCompleto = ciudadRepository.findById(ruta.getDestino().getId())
                .orElseThrow(() -> new IllegalArgumentException("Ciudad destino no encontrada"));

        ruta.setDestino(destinoCompleto);  // asignar el nodo completo
        ciudad.getRutas().add(ruta);

        // Guardar solo la ciudad origen para evitar StackOverflow
        return ciudadRepository.save(ciudad);
    }

    @Override
    public List<Ruta> obtenerRutasDeCiudad(Long ciudadId) {
        Ciudad ciudad = ciudadRepository.findById(ciudadId)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));
        return ciudad.getRutas();
    }

    @Override
    public Ciudad actualizarRuta(Long ciudadId, int rutaIndex, Ruta rutaActualizada) {
        Ciudad ciudad = ciudadRepository.findById(ciudadId)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));

        if (rutaIndex < 0 || rutaIndex >= ciudad.getRutas().size()) {
            throw new IllegalArgumentException("Índice de ruta inválido: " + rutaIndex);
        }

        Ciudad destinoCompleto = ciudadRepository.findById(rutaActualizada.getDestino().getId())
                .orElseThrow(() -> new IllegalArgumentException("Ciudad destino no encontrada"));

        rutaActualizada.setDestino(destinoCompleto);
        ciudad.getRutas().set(rutaIndex, rutaActualizada);
        return ciudadRepository.save(ciudad);
    }

    @Override
    public Ciudad eliminarRutaPorId(Long ciudadId, Long rutaId) {
        Ciudad ciudad = ciudadRepository.findById(ciudadId)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));

        boolean removed = ciudad.getRutas().removeIf(ruta -> ruta.getId().equals(rutaId));
        if (!removed) {
            throw new IllegalArgumentException("Ruta no encontrada con ID: " + rutaId);
        }

        return ciudadRepository.save(ciudad);
    }
}
package com.progra.tp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.progra.tp.model.Ciudad;
import com.progra.tp.model.Ruta;
import com.progra.tp.model.dtos.CiudadResponseDTO;
import com.progra.tp.model.dtos.RutaDTO;
import com.progra.tp.model.dtos.RutaResponseDTO;
import com.progra.tp.repository.CiudadRepository;
import com.progra.tp.service.interfaces.IRutaService;

@Service
public class RutaService implements IRutaService {

    private final CiudadRepository ciudadRepository;

    public RutaService(CiudadRepository ciudadRepository) {
        this.ciudadRepository = ciudadRepository;
    }

    
    @Override
    public CiudadResponseDTO agregarRuta(Long ciudadId, RutaDTO rutaDTO) {
        Ciudad ciudadOrigen = ciudadRepository.findById(ciudadId)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad origen no encontrada"));

        Ciudad ciudadDestino = ciudadRepository.findById(rutaDTO.getDestinoId())
                .orElseThrow(() -> new IllegalArgumentException("Ciudad destino no encontrada"));

        Ruta ruta = new Ruta(ciudadDestino, rutaDTO.getDistancia());
        ciudadOrigen.getRutas().add(ruta);

        Ciudad guardada = ciudadRepository.save(ciudadOrigen);
        return guardada.toDTO();
    }

    @Override
    public List<RutaResponseDTO> obtenerRutasDeCiudad(Long ciudadId) {
        Ciudad ciudad = ciudadRepository.findById(ciudadId)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));

        return ciudad.getRutas().stream()
                .map(r -> new RutaResponseDTO(r.getId(), r.getDestino().getId(), r.getDestino().getNombre(), r.getDistancia()))
                .toList();
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
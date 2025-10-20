package com.progra.tp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.progra.tp.model.Ciudad;
import com.progra.tp.repository.CiudadRepository;
import com.progra.tp.service.interfaces.ICiudadService;

@Service
public class CiudadService implements ICiudadService {

    private final CiudadRepository ciudadRepository;

    public CiudadService(CiudadRepository ciudadRepository) {
        this.ciudadRepository = ciudadRepository;
    }

    @Override
    public Ciudad crearCiudad(Ciudad ciudad) {
        Optional<Ciudad> existente = ciudadRepository.findByNombre(ciudad.getNombre());
        return existente.orElseGet(() -> ciudadRepository.save(ciudad));
    }

    @Override
    public List<Ciudad> getAllCiudades() {
        return ciudadRepository.findAll();
    }

    @Override
    public Ciudad getCiudadByNombre(String nombre) {
        return ciudadRepository.findByNombre(nombre).orElse(null);
    }
}

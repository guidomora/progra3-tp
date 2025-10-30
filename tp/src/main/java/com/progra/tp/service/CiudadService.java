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
        if (ciudad.getNombre() == null || ciudad.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la ciudad no puede ser nulo ni vacío");
        }

        // Normaliza el formato del nombre (primera letra en mayúscula)
        String nombreNormalizado = capitalizeWords(ciudad.getNombre().trim());

        // Verifica si ya existe una ciudad con ese nombre
        return ciudadRepository.findByNombre(nombreNormalizado)
                .orElseGet(() -> {
                    ciudad.setNombre(nombreNormalizado);
                    return ciudadRepository.save(ciudad);
                });
    }

    @Override
    public Ciudad actualizarCiudad(Long id, Ciudad ciudadDetails) {
        Ciudad existingCiudad = getCiudadById(id);
        if (existingCiudad == null) {
            throw new IllegalArgumentException("Ciudad no encontrada con ID: " + id);
        }
        ciudadDetails.setId(id); // Asegura que el ID se mantenga para actualización
        return crearCiudad(ciudadDetails); // Reutiliza lógica de creación con validaciones
    }

    @Override
    public List<Ciudad> getAllCiudades() {
        return ciudadRepository.findAll();
    }
    @Override
    public Ciudad getCiudadByNombre(String nombre) {
        return ciudadRepository.findByNombre(nombre).orElse(null);
    }

    @Override
    public Ciudad getCiudadById(Long id) {
        return ciudadRepository.findById(id).orElse(null);
    }
    
    @Override
    public boolean eliminarCiudad(Long id) {
        Optional<Ciudad> ciudadOpt = ciudadRepository.findById(id);
        if (ciudadOpt.isPresent()) {
            Ciudad ciudad = ciudadOpt.get();
            ciudad.getRutas().clear();
            ciudadRepository.save(ciudad);
            ciudadRepository.deleteById(id);
            return true;
        }
        return false;
    }


    // Metodos privados
    private String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] words = input.toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
            }
        }
        return result.toString().trim();
    }

}

package com.progra.tp.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.progra.tp.model.Agente;
import com.progra.tp.model.Ciudad;
import com.progra.tp.model.Ruta;
import com.progra.tp.model.dtos.AgenteListIdDTO;
import com.progra.tp.model.dtos.AgentePDDTO;
import com.progra.tp.model.dtos.AgentesPDDTO;
import com.progra.tp.model.dtos.CiudadDTO;
import com.progra.tp.repository.AgenteRepository;
import com.progra.tp.repository.CiudadRepository;
import com.progra.tp.service.interfaces.ICiudadService;

import jakarta.transaction.Transactional;

@Service
public class CiudadService implements ICiudadService {

    private final CiudadRepository ciudadRepository;
    private final AgenteRepository agenteRepository;
    public CiudadService(CiudadRepository ciudadRepository,AgenteRepository agenteRepository) {
        this.ciudadRepository = ciudadRepository;
        this.agenteRepository=agenteRepository;
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

    @Override
    @Transactional
    public AgentesPDDTO tareasSegunDistancia(AgenteListIdDTO agentesIds) { //complejidad O( A . R . E) (cantidadAgentes. cantidadRutas. energiaDisponible)

        AgentesPDDTO respuesta = new AgentesPDDTO();

        for (Long agenteId : agentesIds.getAgentesIds()) {

            Agente agente = agenteRepository.findById(agenteId)
                    .orElseThrow(() -> new IllegalArgumentException("No existe agente con id " + agenteId));

            double energia = agente.getEnergiaDisponible();
            Ciudad origen = agente.getUbicacionActual();

            // lista de ciudades candidatas
            List<Ciudad> candidatas = new ArrayList<>();
            List<Double> costos = new ArrayList<>();

            if (origen.getRutas() != null) {
                for (Ruta r : origen.getRutas()) {
                    Ciudad destino = r.getDestino();
                    if (destino == null) continue;

                    double costo = r.getDistancia();
                    if (costo <= energia) {
                        candidatas.add(destino);
                        costos.add(costo);
                    }
                }
            }       

            int ciudadesAlcanzables = costos.size(); // cuantas ciudades puede alzancar el agente con su energia
            double[][] matriz = new double[ciudadesAlcanzables + 1][(int) energia + 1];

            // maximiza cantidad de ciudades
            for (int i = 1; i <= ciudadesAlcanzables; i++) {
                double peso = costos.get(i - 1);
                for (int e = 0; e <= (int) energia; e++) {
                    matriz[i][e] = matriz[i - 1][e]; // no tomar
                    if (peso <= e) {
                        matriz[i][e] = Math.max(matriz[i][e], matriz[i - 1][(int) (e - peso)] + 1);
                    }
                }
            }

            // backtracking para recuperar las ciudades
            List<CiudadDTO> seleccionadasDTO = new ArrayList<>();
            double cap = energia;
            for (int i = ciudadesAlcanzables; i > 0; i--) {
                double peso = costos.get(i - 1);
                if (peso <= cap && matriz[i][(int) cap] == matriz[i - 1][(int) (cap - peso)] + 1) {
                    Ciudad c = candidatas.get(i - 1);
                    CiudadDTO dto = new CiudadDTO();
                    dto.setId(c.getId());
                    dto.setNombre(c.getNombre());
                    seleccionadasDTO.add(dto);
                    cap -= peso;
                }
            }
            Collections.reverse(seleccionadasDTO);

            double energiaGastada = energia - cap;

            respuesta.addResultado(new AgentePDDTO(
                    agente.getId(),
                    seleccionadasDTO,
                    energiaGastada
            ));
        }

        return respuesta;
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

package com.progra.tp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.LinkedList;

import org.springframework.stereotype.Service;

import com.progra.tp.model.Ciudad;
import com.progra.tp.model.Ruta;
import com.progra.tp.model.dtos.CiudadResponseDTO;
import com.progra.tp.model.dtos.CiudadRutaDTO;
import com.progra.tp.model.dtos.RutaDTO;
import com.progra.tp.model.dtos.RutaOptimaResponseDTO;
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

        // crear relacion sin añadir toda la ciudadDestino a la lista de rutas para evitar recursion
        Ruta ruta = new Ruta();
        ruta.setDestino(ciudadDestino);
        ruta.setDistancia(rutaDTO.getDistancia());
        // agrego el peaje
        ruta.setPeaje(rutaDTO.getPeaje()); 

        // guarda la relación usando un metodo de la ciudad
        ciudadOrigen.getRutas().add(ruta);

        // guarda solo la ciudadOrigen
        Ciudad guardada = ciudadRepository.save(ciudadOrigen);
        return guardada.toDTO();
    }

    @Override
    public List<RutaResponseDTO> obtenerRutasDeCiudad(Long ciudadId) {
        Ciudad ciudad = ciudadRepository.findById(ciudadId)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));

        return ciudad.getRutas().stream()
                .map(r -> new RutaResponseDTO(r.getId(), r.getDestino().getId(), r.getDestino().getNombre(),
                        r.getDistancia(),r.getPeaje())) //añado el get peaje en base a la modificación de rutaDTO
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

    
    @Override
    public RutaOptimaResponseDTO calcularRutaMasCorta(Long ciudadOrigenId, Long ciudadDestinoId) {
        Map<Long, Ciudad> grafo = cargarCiudadesComoMapa();

        Ciudad origen = obtenerCiudad(grafo, ciudadOrigenId);
        Ciudad destino = obtenerCiudad(grafo, ciudadDestinoId);

        Map<Long, Double> distancias = new HashMap<>();
        Map<Long, Long> previos = new HashMap<>();
        Set<Long> visitados = new HashSet<>();
        PriorityQueue<NodoDistancia> cola = new PriorityQueue<>((a, b) -> Double.compare(a.distancia(), b.distancia()));

        for (Long id : grafo.keySet()) {
            distancias.put(id, Double.POSITIVE_INFINITY);
        }
        distancias.put(origen.getId(), 0d);
        cola.offer(new NodoDistancia(origen.getId(), 0d));

        while (!cola.isEmpty()) {
            NodoDistancia actual = cola.poll();
            if (!visitados.add(actual.ciudadId())) {
                continue;
            }

            if (actual.ciudadId().equals(destino.getId())) {
                break;
            }

            Ciudad ciudadActual = grafo.get(actual.ciudadId());
            if (ciudadActual.getRutas() == null) {
                continue;
            }

            for (Ruta ruta : ciudadActual.getRutas()) {
                Ciudad vecino = ruta.getDestino();
                if (vecino == null || vecino.getId() == null) {
                    continue;
                }

                double nuevaDistancia = actual.distancia() + ruta.getDistancia();
                if (nuevaDistancia < distancias.getOrDefault(vecino.getId(), Double.POSITIVE_INFINITY)) {
                    distancias.put(vecino.getId(), nuevaDistancia);
                    previos.put(vecino.getId(), ciudadActual.getId());
                    cola.offer(new NodoDistancia(vecino.getId(), nuevaDistancia));
                }
            }
        }

        double distanciaFinal = distancias.getOrDefault(destino.getId(), Double.POSITIVE_INFINITY);
        if (Double.isInfinite(distanciaFinal)) {
            throw new IllegalArgumentException("No existe una ruta disponible entre las ciudades indicadas");
        }

        List<Long> rutaIds = reconstruirRuta(previos, origen.getId(), destino.getId());
        List<CiudadRutaDTO> recorrido = rutaIds.stream()
                .map(id -> {
                    Ciudad ciudad = grafo.get(id);
                    if (ciudad == null) {
                        throw new IllegalArgumentException("Ciudad no encontrada durante la reconstrucción de la ruta");
                    }
                    return new CiudadRutaDTO(ciudad.getId(), ciudad.getNombre());
                })
                .collect(Collectors.toList());

        return new RutaOptimaResponseDTO(recorrido, distanciaFinal);
    }

    private Ciudad obtenerCiudad(Map<Long, Ciudad> grafo, Long ciudadId) {
        Ciudad ciudad = grafo.get(ciudadId);
        if (ciudad == null) {
            throw new IllegalArgumentException("Ciudad no encontrada con ID: " + ciudadId);
        }
        return ciudad;
    }

    private Map<Long, Ciudad> cargarCiudadesComoMapa() {
        List<Ciudad> ciudades = new ArrayList<>();
        ciudadRepository.findAll().forEach(ciudades::add);

        if (ciudades.isEmpty()) {
            throw new IllegalArgumentException("No hay ciudades registradas en el sistema");
        }

        Map<Long, Ciudad> grafo = new HashMap<>();
        for (Ciudad ciudad : ciudades) {
            grafo.put(ciudad.getId(), ciudad);
        }
        return grafo;
    }

    private List<Long> reconstruirRuta(Map<Long, Long> previos, Long origenId, Long destinoId) {
        List<Long> ruta = new ArrayList<>();
        Long actual = destinoId;
        ruta.add(actual);

        while (!actual.equals(origenId)) {
            actual = previos.get(actual);
            if (actual == null) {
                throw new IllegalArgumentException("No existe una ruta disponible entre las ciudades indicadas");
            }
            ruta.add(actual);
        }

        java.util.Collections.reverse(ruta);
        return ruta;
    }

    private record NodoDistancia(Long ciudadId, double distancia) {
    }

//poda por presupuesto
//complejidad computacional
    @Override
    public List<List<Ciudad>> encontrarRutasPorPresupuesto(Long origenId, Long destinoId, double presupuestoMaximo) {
        
        List<List<Ciudad>> caminosEncontrados = new ArrayList<>();
        Optional<Ciudad> optOrigen = this.ciudadRepository.findByIdWithRutas(origenId);
        Optional<Ciudad> optDestino = this.ciudadRepository.findById(destinoId);
    
        if (optOrigen.isEmpty()) {
            throw new IllegalArgumentException("Ciudad de origen no encontrada: " + origenId);}
        if (optDestino.isEmpty()) {
            throw new IllegalArgumentException("Ciudad de destino no encontrada: " + destinoId);}

        Ciudad origen = optOrigen.get();
        Ciudad destino = optDestino.get();
        LinkedList<Ciudad> caminoActual = new LinkedList<>();

        _backtrackPresupuestoRecursive(origen, destino, presupuestoMaximo, 0.0, caminoActual, caminosEncontrados);
        return caminosEncontrados;
    }

    private void _backtrackPresupuestoRecursive(
            Ciudad ciudadActual,
            Ciudad ciudadDestino,
            double presupuestoMaximo,
            double costoAcumulado, 
            LinkedList<Ciudad> caminoActual,
            List<List<Ciudad>> caminosEncontrados
    ) {
        

        caminoActual.addLast(ciudadActual);

        if (ciudadActual.equals(ciudadDestino)) {
            caminosEncontrados.add(new ArrayList<>(caminoActual));
            caminoActual.removeLast();
            return;
        }

        for (Ruta ruta : ciudadActual.getRutas()) { 

            Ciudad proximaCiudad = ruta.getDestino();
            double peajeDelTramo = ruta.getPeaje(); 
            if (costoAcumulado + peajeDelTramo > presupuestoMaximo) {
                continue; // Poda: Excede el presupuesto
            }

            if (caminoActual.contains(proximaCiudad)) {
                continue;
            }

            _backtrackPresupuestoRecursive(
                proximaCiudad, 
                ciudadDestino, 
                presupuestoMaximo, 
                costoAcumulado + peajeDelTramo,
                caminoActual, 
                caminosEncontrados
            );
        }
        caminoActual.removeLast();
    }
}
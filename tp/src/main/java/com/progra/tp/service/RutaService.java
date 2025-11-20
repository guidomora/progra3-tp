package com.progra.tp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Queue;
import java.util.Collections;

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
    public Ciudad actualizarRuta(Long ciudadId, Long rutaId, Ruta rutaActualizada) {
        Ciudad ciudad = ciudadRepository.findById(ciudadId)
                .orElseThrow(() -> new IllegalArgumentException("Ciudad no encontrada"));

        Ruta rutaExistente = ciudad.getRutas().stream()
                .filter(r -> r.getId().equals(rutaId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada"));

        if (rutaActualizada.getPeaje() != null) {
            rutaExistente.setPeaje(rutaActualizada.getPeaje());
        }

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

        List<Ciudad> subgrafo = ciudadRepository.cargarSubgrafo(origenId);

        Ciudad origen = subgrafo.stream()
            .filter(c -> c.getId().equals(origenId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Origen no encontrado."));

        Ciudad destino = subgrafo.stream()
            .filter(c -> c.getId().equals(destinoId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Destino no alcanzable desde el origen."));

        List<List<Ciudad>> caminosEncontrados = new ArrayList<>();
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

        if (ciudadActual.getId().equals(ciudadDestino.getId())) {
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


    //BFS para ruta con menos escalas
    //
    @Override
    public RutaOptimaResponseDTO rutaConMenosEscalas(Long origenId, Long destinoId) {
        // Asumimos que cada carga de un Subgrafo tiene un costo de  V + E del subgrafo.
        List<Ciudad> grafo = ciudadRepository.cargarSubgrafo(origenId);

        Ciudad origen = null; // 1
        Ciudad destino = null; // 1
        
        for (Ciudad c : grafo) { // V + 1
            if (c.getId().equals(origenId)) origen = c; // V
            if (c.getId().equals(destinoId)) destino = c; // V
        }

        if (origen == null || destino == null) { // 1
            throw new IllegalArgumentException("Ciudad origen o destino no encontrada en el grafo conectado");
        }


        Queue<Ciudad> cola = new LinkedList<>(); // 1
        Set<Long> visitados = new HashSet<>();   // 1
        Map<Long, Long> padres = new HashMap<>(); // 1

        cola.offer(origen);            // 1
        visitados.add(origen.getId()); // 1
        padres.put(origen.getId(), null); // 1

        boolean encontrado = false; // 1
        
        // En el peor caso (grafo conexo), cada vértice entra a la cola una sola vez.
        while (!cola.isEmpty()) { // V + 1
            
            Ciudad actual = cola.poll(); // V (Operación O(1) repetida V veces)

            if (actual.getId().equals(destino.getId())) { // V
                encontrado = true; // 1
                break;
            }

            // Se ejecuta una vez por cada ARISTA (E) en todo el proceso, porque cada nodo se procesa una vez.
            for (Ruta ruta : actual.getRutas()) { // E (Total acumulado)
                
                Ciudad vecino = ruta.getDestino(); // E
                Long vecinoId = vecino.getId();    // E

                if (!visitados.contains(vecinoId)) { // E (HashSet es O(1))
                    visitados.add(vecinoId);       // V (Máximo V inserciones)
                    padres.put(vecinoId, actual.getId()); // V
                    cola.offer(vecino);            // V
                }
            }
        }

        if (!encontrado) { // 1
            throw new IllegalArgumentException("No existe ruta entre las ciudades seleccionadas");
        }

        return reconstruirRutaBFS(padres, grafo, destinoId); // O(V)
    }

    /*
     * Complejidad: O(V) en el peor caso (si el camino es una línea recta que recorre todo el grafo).
     */
    private RutaOptimaResponseDTO reconstruirRutaBFS(Map<Long, Long> padres, List<Ciudad> grafo, Long destinoId) {
        List<CiudadRutaDTO> recorrido = new ArrayList<>(); // 1
        Long actualId = destinoId; // 1
        
        while (actualId != null) { // V (Largo del camino)
            
            // asumimos búsqueda O(1) para el análisis.
            Long idBusqueda = actualId; 
            Ciudad ciudad = grafo.stream()
                .filter(c -> c.getId().equals(idBusqueda))
                .findFirst()
                .orElseThrow(); 
            
            recorrido.add(new CiudadRutaDTO(ciudad.getId(), ciudad.getNombre())); // V
            actualId = padres.get(actualId); // V
        }
        
        Collections.reverse(recorrido); // V
        
        double saltos = recorrido.size() - 1; // 1
        
        return new RutaOptimaResponseDTO(recorrido, saltos);

        
    //Costo Total = O(V) + O(V) + O(E) + O(V^2)
    //termino dominante = O(V^2)
    }
}
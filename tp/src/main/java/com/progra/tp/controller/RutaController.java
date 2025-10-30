package com.progra.tp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.progra.tp.model.Ciudad;
import com.progra.tp.model.Ruta;
import com.progra.tp.model.dtos.CiudadResponseDTO;
import com.progra.tp.model.dtos.RutaDTO;
import com.progra.tp.model.dtos.RutaOptimaResponseDTO;
import com.progra.tp.model.dtos.RutaResponseDTO;
import com.progra.tp.service.interfaces.IRutaService;

@RestController
@RequestMapping("/api/ciudades/{ciudadId}/rutas")

public class RutaController {

    @Autowired
    private IRutaService rutaService;

    @GetMapping
    public ResponseEntity<List<RutaResponseDTO>> obtenerRutas(@PathVariable Long ciudadId) {
        try {
            List<RutaResponseDTO> rutas = rutaService.obtenerRutasDeCiudad(ciudadId);
            return ResponseEntity.ok(rutas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/agregar")
    public ResponseEntity<CiudadResponseDTO> agregarRuta(@PathVariable Long ciudadId, @RequestBody RutaDTO rutaDTO) {
        try {
            CiudadResponseDTO ciudad = rutaService.agregarRuta(ciudadId, rutaDTO);
            return ResponseEntity.ok(ciudad);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/optima/{destinoId}")
    public ResponseEntity<RutaOptimaResponseDTO> obtenerRutaMasCorta(@PathVariable Long ciudadId,
            @PathVariable Long destinoId) {
        try {
            RutaOptimaResponseDTO respuesta = rutaService.calcularRutaMasCorta(ciudadId, destinoId);
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{rutaIndex}")
    public ResponseEntity<Ciudad> actualizarRuta(@PathVariable Long ciudadId, @PathVariable int rutaIndex,
            @RequestBody Ruta ruta) {
        try {
            Ciudad ciudad = rutaService.actualizarRuta(ciudadId, rutaIndex, ruta);
            return ResponseEntity.ok(ciudad);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/ruta/{rutaId}")
    public ResponseEntity<Ciudad> eliminarRutaPorId(@PathVariable Long ciudadId, @PathVariable Long rutaId) {
        try {
            Ciudad ciudad = rutaService.eliminarRutaPorId(ciudadId, rutaId);
            return ResponseEntity.ok(ciudad);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
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
import com.progra.tp.service.interfaces.ICiudadService;

@RestController
@RequestMapping("/api/ciudades")
public class CiudadController {

    @Autowired
    private ICiudadService ciudadService;

    @GetMapping
    public List<Ciudad> getAllCiudades() {
        return ciudadService.getAllCiudades();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ciudad> getCiudadById(@PathVariable Long id) {
        Ciudad ciudad = ciudadService.getCiudadById(id);
        if (ciudad != null) {
            return ResponseEntity.ok(ciudad);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Ciudad> getCiudadByNombre(@PathVariable String nombre) {
        Ciudad ciudad = ciudadService.getCiudadByNombre(nombre);
        if (ciudad != null) {
            return ResponseEntity.ok(ciudad);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/crearCiudad")
    public Ciudad createCiudad(@RequestBody Ciudad ciudad) {
        return ciudadService.crearCiudad(ciudad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ciudad> updateCiudad(@PathVariable Long id, @RequestBody Ciudad ciudadDetails) {
        Ciudad existingCiudad = ciudadService.getCiudadById(id);
        if (existingCiudad != null) {
            ciudadDetails.setId(id); // Asegura que el ID se mantenga para actualización
            Ciudad updatedCiudad = ciudadService.crearCiudad(ciudadDetails); // Reutiliza lógica de creación con validaciones
            return ResponseEntity.ok(updatedCiudad);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCiudad(@PathVariable Long id) {
        boolean eliminada = ciudadService.eliminarCiudad(id);
        if (eliminada) {
            return ResponseEntity.noContent().build(); // 204 - Eliminado correctamente
        }
        return ResponseEntity.notFound().build(); // 404 - No se encontró
    }
}
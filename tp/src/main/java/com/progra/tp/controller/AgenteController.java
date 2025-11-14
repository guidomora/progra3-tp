package com.progra.tp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.progra.tp.model.Agente;
import com.progra.tp.model.dtos.AgenteAsignacionDTO;
import com.progra.tp.service.interfaces.IAgenteService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequestMapping("/api/agentes")
@RestController
public class AgenteController {
    private final IAgenteService agenteService;
    
    public AgenteController (IAgenteService agenteService){
        this.agenteService=agenteService;
    }

    @GetMapping
    public List<Agente> getAllAgentes(){
        List<Agente> agentes=agenteService.getAllAgentes();
        return agentes;
    }

    @PostMapping("/crearAgente")
    public ResponseEntity<Agente> crearAgente(@RequestBody Agente agente) {
        Agente nuevoAgente = agenteService.crearAgente(agente); // guarda en Neo4j
        return ResponseEntity.ok(nuevoAgente);
    }

    @PutMapping("/{agenteId}")
    public ResponseEntity<AgenteAsignacionDTO> actualizarAgente(
            @PathVariable Long agenteId,
            @RequestBody AgenteAsignacionDTO agenteActualizado) {
        try {
            AgenteAsignacionDTO agenteDTO = agenteService.actualizarAgente(agenteId, agenteActualizado);
            return ResponseEntity.ok(agenteDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

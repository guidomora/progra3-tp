package com.progra.tp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.progra.tp.model.dtos.AgenteAsignacionDTO;
import com.progra.tp.model.dtos.AgenteListIdDTO;
import com.progra.tp.model.dtos.AgentesPDDTO;
import com.progra.tp.model.dtos.AsignacionGreedyRequestDTO;
import com.progra.tp.model.dtos.AsignacionGreedyResponseDTO;
import com.progra.tp.model.dtos.MSTResponseDTO;
import com.progra.tp.model.dtos.TareaRequestDTO;
import com.progra.tp.service.interfaces.IAsignacionService;
import com.progra.tp.service.interfaces.ICiudadService;
import com.progra.tp.service.interfaces.IRutaService;
import com.progra.tp.service.interfaces.ITareaService;





@RestController
@RequestMapping("api/rutas/algoritmos")

public class AlgoritmosController {

    @Autowired
    private IRutaService rutaService;

    @Autowired
    private IAsignacionService asignacionService;

    @Autowired
    private ITareaService tareaService;

    @Autowired
    private ICiudadService ciudadService;
    @GetMapping("/mst/prim/{ciudadInicialId}")
    public ResponseEntity<MSTResponseDTO> getMSTPrim(@PathVariable Long ciudadInicialId) {
        try {
            MSTResponseDTO mst = rutaService.calcularMSTPrim(ciudadInicialId);
            return ResponseEntity.ok(mst);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);} 

    }

    @PostMapping("/misiones/greedy")
    public ResponseEntity<AsignacionGreedyResponseDTO> asignarMisionesGreedy(
            @RequestBody AsignacionGreedyRequestDTO request) {
        try {
            AsignacionGreedyResponseDTO respuesta = asignacionService.asignarMisionesGreedy(request);
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/greedy/{agenteId}")
    public ResponseEntity<AgenteAsignacionDTO> getTareasGreedy(@PathVariable Long agenteId, @RequestBody TareaRequestDTO tareasdto) {
        try {
            AgenteAsignacionDTO asignacionDeTareas = tareaService.asignarTareas(agenteId, tareasdto);
           
            return ResponseEntity.ok(asignacionDeTareas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }//Lo que cambia con el de arriba, es que aca solo agregas las tareas optimas locales a UN SOLO agente
    
    @PostMapping("/agentes/pd")
    public ResponseEntity<AgentesPDDTO> tareasSegunDistancia(@RequestBody AgenteListIdDTO agentesIds) {
        //TODO: process POST request
        try {
            AgentesPDDTO agentes=ciudadService.tareasSegunDistancia(agentesIds);
            return ResponseEntity.ok(agentes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        
        }
        
    }
    

}
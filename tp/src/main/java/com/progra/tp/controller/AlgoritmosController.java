package com.progra.tp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.progra.tp.model.dtos.MSTResponseDTO;
import com.progra.tp.service.interfaces.IRutaService;




@RestController
@RequestMapping("api/rutas/algoritmos")

public class AlgoritmosController {

    @Autowired
    private IRutaService rutaService;

    @GetMapping("/mst/prim/{ciudadInicialId}")
    public ResponseEntity<MSTResponseDTO> getMSTPrim(@PathVariable Long ciudadInicialId) {
        try {
            MSTResponseDTO mst = rutaService.calcularMSTPrim(ciudadInicialId);
            return ResponseEntity.ok(mst);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
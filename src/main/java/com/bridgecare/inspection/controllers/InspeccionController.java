package com.bridgecare.inspection.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.bridgecare.inspection.models.dtos.InspeccionDTO;
import com.bridgecare.inspection.services.InspeccionService;

import java.util.List;

@RestController
@RequestMapping("/api/inspeccion")
public class InspeccionController {
    @Autowired
    private InspeccionService inspeccionService;

    @PostMapping("/add")
    public ResponseEntity<String> addInspeccion(@RequestBody InspeccionDTO request, Authentication authentication) {
        Long inspeccionId = inspeccionService.saveInspeccion(request, authentication);
        return ResponseEntity.ok("Inspeccion creada con ID: " + inspeccionId);
    }

    @Transactional(readOnly=true)
    @GetMapping("/{id}")
    public ResponseEntity<InspeccionDTO> getInspeccionById(@PathVariable Long id){
        InspeccionDTO dto = inspeccionService.getInspeccionById(id);
        return ResponseEntity.ok(dto);
    }

    @Transactional(readOnly=true)
    @GetMapping("/puente/{puenteId}")
    public ResponseEntity<List<InspeccionDTO>> getByPuenteId(@PathVariable Long puenteId) {
        return ResponseEntity.ok(inspeccionService.getInspeccionByPuenteId(puenteId));
    }

}
package com.bridgecare.inspection.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bridgecare.inspection.models.dtos.InspeccionDTO;
import com.bridgecare.inspection.services.InspeccionService;

@RestController
@RequestMapping("/api/inspeccion")
public class InspeccionController {
    @Autowired
    private InspeccionService inspeccionService;

    @PostMapping("/add")
    public ResponseEntity<String> addInspeccion(@RequestBody InspeccionDTO request, Authentication authentication) throws IOException {
        Long inspeccionId = inspeccionService.saveInspeccion(request, authentication);
        return ResponseEntity.ok("{\"message\": \"Inspeccion created with ID: " + inspeccionId + "\"}");
    }
    
    @DeleteMapping("/delete/by-puente/{puenteId}")
    public ResponseEntity<String> deleteByPuente(@PathVariable Long puenteId) {
        inspeccionService.deleteByPuenteId(puenteId);
        return ResponseEntity.ok("Inspección(es) eliminada(s)");
    }
}
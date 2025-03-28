package com.bridgecare.inspection.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> createInspeccion(@RequestBody InspeccionDTO request) {
        Long inspeccionId = inspeccionService.saveInspection(request);
        return ResponseEntity.ok("Inspeccion created with ID: " + inspeccionId);
    }
}
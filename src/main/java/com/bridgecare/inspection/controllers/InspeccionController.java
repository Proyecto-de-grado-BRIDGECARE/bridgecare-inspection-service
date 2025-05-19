package com.bridgecare.inspection.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgecare.inspection.models.dtos.InspeccionDTO;
import com.bridgecare.inspection.services.InspeccionService;

import java.io.IOException;

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

    @PostMapping("/image/upload/{parentFormId}/{formUuid}/{sectionUuid}/{imageUuid}")
    public ResponseEntity<String> uploadImageChunk(
            @PathVariable String parentFormId,
            @PathVariable String formUuid,
            @PathVariable String sectionUuid,
            @PathVariable String imageUuid,
            @RequestParam("chunk") int chunk,
            @RequestParam("total") int total,
            @RequestBody byte[] chunkData) throws IOException {
        inspeccionService.saveImageChunk(parentFormId, formUuid, sectionUuid, imageUuid, chunk, total, chunkData);
        return ResponseEntity.ok("Chunk " + chunk + " uploaded successfully");
    }
}

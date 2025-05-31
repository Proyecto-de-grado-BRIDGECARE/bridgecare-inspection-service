package com.bridgecare.inspection.controllers;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bridgecare.inspection.models.dtos.InspeccionDTO;
import com.bridgecare.inspection.services.InspeccionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/inspeccion")
public class InspeccionController {
    @Autowired
    private InspeccionService inspeccionService;
    private final ObjectMapper objectMapper;

    public InspeccionController(ObjectMapper objectMapper, InspeccionService inspeccionService) {
        this.objectMapper = objectMapper;
        this.inspeccionService = inspeccionService;
    }

    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addInspeccion(
            @RequestPart("inspeccion") String inspeccionJson,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication) {
        try {
            InspeccionDTO request = objectMapper.readValue(inspeccionJson, InspeccionDTO.class);
            Long inspeccionId = inspeccionService.saveInspeccion(request, images, authentication);
            return ResponseEntity.ok("Inspeccion creada con ID: " + inspeccionId);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al procesar la solicitud: " + e.getMessage());
        }
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

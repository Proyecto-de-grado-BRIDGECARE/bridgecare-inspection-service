package com.bridgecare.inspection.services;

import com.bridgecare.inspection.models.dtos.ComponenteDTO;
import com.bridgecare.inspection.models.dtos.InspeccionDTO;
import com.bridgecare.inspection.models.entities.Componente;
import com.bridgecare.inspection.models.entities.Inspeccion;
import com.bridgecare.common.models.entities.Puente;
import com.bridgecare.common.models.entities.Usuario;
import com.bridgecare.inspection.models.entities.Reparacion;
import com.bridgecare.inspection.repositories.ComponenteRepository;
import com.bridgecare.inspection.repositories.InspeccionRepository;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InspeccionService {

    @Autowired
    private InspeccionRepository inspeccionRepository;

    @Autowired
    private ComponenteRepository componenteRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${file.storage.path}")
    private String storagePath;

    @Transactional
    public Long saveInspeccion(InspeccionDTO request, Authentication authentication) throws IOException {
        // Validate authentication
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Unauthorized: No valid token provided");
        }

        // Validate DTO
        if (request == null || request.getPuente() == null || request.getPuente().getId() == null) {
            throw new IllegalArgumentException("Invalid InspeccionDTO or Puente");
        }

        // Extract user email from JWT
        String userEmail = extractUserEmailFromAuthentication(authentication);
        System.out.println("Processing inspeccion for user: " + userEmail + ".");

        // Fetch Puente via REST API
        String puenteUrl = "http://localhost:8081/api/puentes/" + request.getPuente().getId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getTokenFromAuthentication(authentication));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Puente> response = restTemplate.exchange(puenteUrl, HttpMethod.GET, entity, Puente.class);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IllegalStateException("Failed to find Puente with ID: " + request.getPuente().getId());
        }
        Puente puente = response.getBody();

        // Create and save Inspeccion
        Inspeccion inspeccion = new Inspeccion();
        inspeccion.setPuente(puente);
        inspeccion.setTiempo(request.getTiempo());
        inspeccion.setTemperatura(request.getTemperatura());
        inspeccion.setAdministrador(request.getAdministrador());
        inspeccion.setAnioProximaInspeccion(request.getAnioProximaInspeccion());
        inspeccion.setObservacionesGenerales(request.getObservacionesGenerales());
        inspeccion.setFecha(request.getFecha());

        if (request.getUsuario() != null) {
            Usuario usuario = mapDTOToEntity(request.getUsuario(), Usuario.class);
            inspeccion.setUsuario(usuario);
        } else {
            inspeccion.setUsuario(null);
        }

        Inspeccion savedInspeccion = inspeccionRepository.save(inspeccion);

        // Process Componentes
        for (ComponenteDTO componenteDTO : request.getComponentes()) {
            Componente componente = new Componente();
            componente.setNombre(componenteDTO.getNomb());
            componente.setCalificacion(componenteDTO.getCalificacion());
            componente.setMantenimiento(componenteDTO.getMantenimiento());
            componente.setInspEsp(componenteDTO.getInspEesp());
            componente.setNumeroFotos(componenteDTO.getNumeroFfotos());
            componente.setTipoDanio(componenteDTO.getTipoDanio());
            componente.setDanio(componenteDTO.getDanio());
            componente.setInspeccion(savedInspeccion);

            // Process Reparaciones
            List<Reparacion> reparaciones = componenteDTO.getReparacion().stream().map(reparacionDTO -> {
                Reparacion reparacion = new Reparacion();
                reparacion.setTipo(reparacionDTO.getTipo());
                reparacion.setCantidad(reparacionDTO.getCantidad());
                reparacion.setAnio(reparacionDTO.getAnio());
                reparacion.setCosto(reparacionDTO.getCosto());
                reparacion.setComponente(componente);
                return reparacion;
            }).collect(Collectors.toList());

            componente.setReparaciones(reparaciones);

            // Handle image paths
            Path oldImagePath = Paths.get(storagePath, puente.getId().toString(), request.getInspeccionUuid(),
                    componenteDTO.getComponenteUuid());
            List<String> updatedImagePaths = new ArrayList<>();

            // Save Componente
            Componente savedComponente = componenteRepository.save(componente);

            // Move images
            if (Files.exists(oldImagePath)) {
                Path newImagePath = Paths.get(storagePath, puente.getId().toString(),
                        savedInspeccion.getId().toString(),
                        savedComponente.getId().toString());
                Files.createDirectories(newImagePath);

                try (Stream<Path> imagePaths = Files.list(oldImagePath)) {
                    imagePaths.forEach(imagePath -> {
                        Path targetPath = null;
                        try {
                            targetPath = newImagePath.resolve(imagePath.getFileName().toString());
                            Files.move(imagePath, targetPath);
                            updatedImagePaths.add(targetPath.toString());
                        } catch (IOException e) {
                            System.out.println("Failed to move image from " + imagePath + " to " + targetPath + ", skipping deletion");
                        }
                    });
                }

                // Delete old directory if empty
                try (Stream<Path> files = Files.list(oldImagePath)) {
                    if (files.findAny().isEmpty()) {
                        Files.deleteIfExists(oldImagePath);
                    } else {
                        System.out.println("Directory " + oldImagePath + " is not empty, skipping deletion");
                    }
                }
            }

            // Update Componente with image paths
            savedComponente.setImagePaths(updatedImagePaths);
            componenteRepository.save(savedComponente);
        }

        // Delete inspeccion UUID directory if empty
        Path inspeccionPath = Paths.get(storagePath, puente.getId().toString(), request.getInspeccionUuid());
        try (Stream<Path> files = Files.list(inspeccionPath)) {
            if (files.findAny().isEmpty()) {
                Files.deleteIfExists(inspeccionPath);
            } else {
                System.out.println("Directory " + inspeccionPath + " is not empty, skipping deletion");
            }
        }

        return savedInspeccion.getId();
    }

    private <D, E> E mapDTOToEntity(D dto, Class<E> entityClass) {
        if (dto == null)
            return null;
        try {
            E entity = entityClass.getDeclaredConstructor().newInstance();

            Field[] dtoFields = dto.getClass().getDeclaredFields();

            for (Field dtoField : dtoFields) {
                dtoField.setAccessible(true);
                try {
                    Field entityField = entityClass.getDeclaredField(dtoField.getName());
                    entityField.setAccessible(true);
                    Object dtoValue = dtoField.get(dto);
                    if (dtoValue == null) {
                        entityField.set(entity, null);
                    } else if (dtoField.getType().isPrimitive() ||
                            dtoField.getType().getName().startsWith("java.lang") ||
                            dtoField.getType().getName().startsWith("java.math") ||
                            dtoField.getType().getName().startsWith("java.time")) {
                        // Primitive, java.lang (String, Boolean), java.math (BigDecimal), java.time
                        // (LocalDate)
                        entityField.set(entity, dtoValue);
                    } else {
                        // Nested object: recursively map it
                        Object entityValue = mapDTOToEntity(dtoValue, entityField.getType());
                        entityField.set(entity, entityValue);
                    }
                } catch (NoSuchFieldException e) {
                    continue;
                }
            }

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping DTO to Entity", e);
        }
    }

    private String extractUserEmailFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof String) {
            String email = (String) authentication.getPrincipal();
            if (email.contains("@")) {
                return email;
            } else {
                throw new IllegalStateException("User email in token is not valid: " + email);
            }
        }
        throw new IllegalStateException("Unable to extract user email from token");
    }

    private String getTokenFromAuthentication(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getCredentials() instanceof String) {
            return (String) authentication.getCredentials();
        }
        throw new IllegalStateException("No JWT token found in authentication");
    }

    @Transactional
    public void deleteByPuenteId(Long puenteId) {
        inspeccionRepository.deleteByPuenteId(puenteId);
    }
}

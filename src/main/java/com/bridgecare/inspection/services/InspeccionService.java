package com.bridgecare.inspection.services;

import com.bridgecare.inspection.config.RabbitMQConfig;
import com.bridgecare.inspection.models.dtos.ComponenteDTO;
import com.bridgecare.inspection.models.dtos.InspeccionDTO;
import com.bridgecare.inspection.models.dtos.InspeccionEventDTO;
import com.bridgecare.inspection.models.dtos.ReparacionDTO;
import com.bridgecare.inspection.models.entities.Componente;
import com.bridgecare.inspection.models.entities.Inspeccion;
import com.bridgecare.common.models.dtos.UsuarioDTO;
import com.bridgecare.common.models.entities.Puente;
import com.bridgecare.common.models.entities.Usuario;
import com.bridgecare.common.models.dtos.PuenteDTO;
import com.bridgecare.inspection.models.entities.Reparacion;
import com.bridgecare.inspection.repositories.InspeccionRepository;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class InspeccionService {

    @Autowired
    private InspeccionRepository inspeccionRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;

    @Value("${file.storage.path}")
    private String storagePath;

    public Long saveInspeccion(InspeccionDTO request, List<MultipartFile> images, Authentication authentication) throws IOException {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Unauthorized: No valid token provided");
        }
    
        // Extract user ID from JWT
        String userEmail = extractUserEmailFromAuthentication(authentication);
        System.out.println("userEmail: " + userEmail);
    
        String puenteUrl = "http://bridge-service:8081/api/puentes/" + request.getPuente().getId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getTokenFromAuthentication(authentication));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Puente> response = restTemplate.exchange(puenteUrl, HttpMethod.GET, entity, Puente.class);
    
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new IllegalStateException("Failed to find Puente with ID: " + request.getPuente().getId());
        }
    
        Puente puente = response.getBody();
        if (puente == null) {
            throw new IllegalStateException("Puente data is null for ID: " + request.getPuente().getId());
        }
    
        Inspeccion inspeccion = new Inspeccion();
        inspeccion.setPuente(puente);
        inspeccion.setTiempo(request.getTiempo());
        inspeccion.setTemperatura(request.getTemperatura());
        inspeccion.setAdministrador(request.getAdministrador());
        inspeccion.setAnioProximaInspeccion(request.getAnioProximaInspeccion());
        inspeccion.setObservacionesGenerales(request.getObservacionesGenerales());
        inspeccion.setFecha(request.getFecha());
    
        // Convertir los componentes de DTO a entidad
        List<Componente> componentes = request.getComponentes().stream().map(dto -> {
            Componente componente = new Componente();
            componente.setNombre(dto.getNomb());
            componente.setCalificacion(dto.getCalificacion());
            componente.setMantenimiento(dto.getMantenimiento());
            componente.setInspEsp(dto.getInspEesp());
            componente.setNumeroFotos(dto.getNumeroFfotos());
            componente.setTipoDanio(dto.getTipoDanio());
            componente.setDanio(dto.getDanio());
    
            // Convertir la lista de ReparacionDTO a Reparacion
            List<Reparacion> reparaciones = dto.getReparacion().stream().map(reparacionDTO -> {
                Reparacion reparacion = new Reparacion();
                reparacion.setTipo(reparacionDTO.getTipo());
                reparacion.setCantidad(reparacionDTO.getCantidad());
                reparacion.setAnio(reparacionDTO.getAnio());
                reparacion.setCosto(reparacionDTO.getCosto());
                reparacion.setComponente(componente);
                return reparacion;
            }).collect(Collectors.toList());
    
            componente.setReparaciones(reparaciones);
            componente.setInspeccion(inspeccion);
            return componente;
        }).collect(Collectors.toList());
    
        inspeccion.setComponentes(componentes);
        Usuario usuario = mapUsuarioDTOToUsuario(request.getUsuario());
        inspeccion.setUsuario(usuario);
        Long idInspeccion = inspeccionRepository.save(inspeccion).getId();
    
        // Save images if provided
        if (images != null && !images.isEmpty()) {
            for (Componente componente : componentes) {
                Path dir = Paths.get("/srv/bridgecare/images/" + puente.getId() + "/" + idInspeccion + "/" + componente.getId());
                Files.createDirectories(dir);
                List<String> imagePaths = new ArrayList<>();
                for (MultipartFile image : images) {
                    String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                    Path filePath = dir.resolve(fileName);
                    Files.write(filePath, image.getBytes());
                    imagePaths.add(filePath.toString());
                }
                componente.setImagePaths(imagePaths);
            }
            inspeccionRepository.save(inspeccion); // Update with image paths
        }
    
        // Construir evento
        InspeccionEventDTO evento = new InspeccionEventDTO();
        evento.setInspeccionId(inspeccion.getId());
        List<InspeccionEventDTO.ComponenteDTO> lista = componentes.stream().map(c -> {
            InspeccionEventDTO.ComponenteDTO dto = new InspeccionEventDTO.ComponenteDTO();
            dto.setNombre(c.getNombre());
            dto.setCalificacion(c.getCalificacion());
            dto.setTipoDanio(c.getTipoDanio());
            return dto;
        }).toList();
        evento.setComponentes(lista);
    
        // Publicar evento
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, evento);
        System.out.println("Evento enviado: inspeccionId=" + inspeccion.getId());
    
        return idInspeccion;
    }

    @Transactional
    public void saveImageChunk(String parentFormId, String formUuid, String sectionUuid, String imageUuid, int chunk,
            int total, byte[] chunkData) throws IOException {
        Path tempDir = Paths.get(storagePath, parentFormId, formUuid, sectionUuid, "temp");
        Path chunkPath = tempDir.resolve(imageUuid + ".part" + chunk);
        Files.createDirectories(tempDir);
        Files.write(chunkPath, chunkData);

        if (chunk + 1 == total) {
            Path finalPath = Paths.get(storagePath, parentFormId, formUuid, sectionUuid, imageUuid + ".jpg");
            Files.createDirectories(finalPath.getParent());
            try (var output = Files.newOutputStream(finalPath, StandardOpenOption.CREATE)) {
                for (int i = 0; i < total; i++) {
                    Path partPath = tempDir.resolve(imageUuid + ".part" + i);
                    output.write(Files.readAllBytes(partPath));
                    Files.deleteIfExists(partPath);
                }
            }
            Files.deleteIfExists(tempDir);
        }
    }

    private Usuario mapUsuarioDTOToUsuario(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioDTO.getId());
        return usuario;
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

    private InspeccionDTO mapToDTO(Inspeccion inspeccion) {
        InspeccionDTO dto = new InspeccionDTO();

        dto.setId(inspeccion.getId());
        dto.setFecha(inspeccion.getFecha());
        dto.setTiempo(inspeccion.getTiempo());
        dto.setTemperatura(inspeccion.getTemperatura());
        dto.setAdministrador(inspeccion.getAdministrador());
        dto.setAnioProximaInspeccion(inspeccion.getAnioProximaInspeccion());
        dto.setObservacionesGenerales(inspeccion.getObservacionesGenerales());

        // Mapear Puente
        PuenteDTO puenteDTO = new PuenteDTO();
        puenteDTO.setId(inspeccion.getPuente().getId());
        puenteDTO.setNombre(inspeccion.getPuente().getNombre());
        dto.setPuente(puenteDTO);

        // Mapear Usuario
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(inspeccion.getUsuario().getId());
        usuarioDTO.setNombres(inspeccion.getUsuario().getNombres());
        usuarioDTO.setApellidos(inspeccion.getUsuario().getApellidos());
        dto.setUsuario(usuarioDTO);

        // Mapear Componentes
        List<ComponenteDTO> componentesDTO = inspeccion.getComponentes().stream().map(componente -> {
            ComponenteDTO cDTO = new ComponenteDTO();
            cDTO.setNomb(componente.getNombre());
            cDTO.setCalificacion(componente.getCalificacion());
            cDTO.setMantenimiento(componente.getMantenimiento());
            cDTO.setInspEesp(componente.getInspEsp());
            cDTO.setNumeroFfotos(componente.getNumeroFotos());
            cDTO.setTipoDanio(componente.getTipoDanio());
            cDTO.setDanio(componente.getDanio());

            // Mapear Reparaciones dentro de cada componente
            List<ReparacionDTO> reparacionesDTO = componente.getReparaciones().stream().map(reparacion -> {
                ReparacionDTO rDTO = new ReparacionDTO();
                rDTO.setTipo(reparacion.getTipo());
                rDTO.setCantidad(reparacion.getCantidad());
                rDTO.setAnio(reparacion.getAnio());
                rDTO.setCosto(reparacion.getCosto());
                return rDTO;
            }).collect(Collectors.toList());

            cDTO.setReparacion(reparacionesDTO);

            return cDTO;
        }).collect(Collectors.toList());

        dto.setComponentes(componentesDTO);

        return dto;
    }

    public InspeccionDTO getInspeccionById(Long id) {
        Inspeccion inspeccion = inspeccionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inpeccion no encontrada con id: " + id));

        return mapToDTO(inspeccion);
    }

    public List<InspeccionDTO> getInspeccionByPuenteId(Long puenteId) {
        Puente puente = new Puente();
        puente.setId(puenteId);

        List<Inspeccion> inspecciones = inspeccionRepository.findByPuente(puente);

        if (inspecciones.isEmpty()) {
            throw new RuntimeException("No se encontró inspección para el puente con ID " + puenteId);
        }

        return inspecciones.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}

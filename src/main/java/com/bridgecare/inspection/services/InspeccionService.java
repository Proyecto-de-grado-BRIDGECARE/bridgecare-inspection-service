package com.bridgecare.inspection.services;

import com.bridgecare.inspection.models.dtos.ComponenteDTO;
import com.bridgecare.inspection.models.dtos.InspeccionDTO;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InspeccionService {

    @Autowired
    private InspeccionRepository inspeccionRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public Long saveInspeccion(InspeccionDTO request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Unauthorized: No valid token provided");
        }

        // Extract user ID from JWT
        String userEmail = extractUserEmailFromAuthentication(authentication);
        System.out.println("userEmail: " + userEmail);

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

            componente.setReparaciones(reparaciones); // Asignar la lista convertida
            componente.setInspeccion(inspeccion); // Asignar inspección al componente
            return componente;
        }).collect(Collectors.toList());

        inspeccion.setComponentes(componentes);

        Usuario usuario = mapUsuarioDTOToUsuario(request.getUsuario());
        inspeccion.setUsuario(usuario);

        return inspeccionRepository.save(inspeccion).getId();
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

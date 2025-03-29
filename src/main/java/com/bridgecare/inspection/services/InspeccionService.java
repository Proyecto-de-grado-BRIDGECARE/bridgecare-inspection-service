package com.bridgecare.inspection.services;

import com.bridgecare.inspection.models.dtos.InspeccionDTO;
import com.bridgecare.inspection.models.entities.Componente;
import com.bridgecare.inspection.models.entities.Inspeccion;
import com.bridgecare.common.models.entities.Puente;
import com.bridgecare.common.models.entities.Usuario;
import com.bridgecare.inspection.models.entities.Reparacion;
import com.bridgecare.inspection.repositories.InspeccionRepository;
import com.bridgecare.common.repositories.PuenteRepository; // Se importa el repositorio de Puente

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InspeccionService {

    @Autowired
    private InspeccionRepository inspeccionRepository;

    @Autowired
    private PuenteRepository puenteRepository; // Se inyecta el repositorio de Puente

    @Transactional
    public Long saveInspeccion(InspeccionDTO request, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Unauthorized: No valid token provided");
        }

        // Extraer el email del usuario autenticado
        String userEmail = extractUserEmailFromAuthentication(authentication);
        System.out.println("userEmail: " + userEmail);

        // Buscar el puente por ID en la base de datos
        Optional<Puente> puenteOpt = puenteRepository.findById(request.getPuenteId());
        if (puenteOpt.isEmpty()) {
            throw new IllegalStateException("Puente no encontrado con ID: " + request.getPuenteId());
        }
        Puente puente = puenteOpt.get();

        Inspeccion inspeccion = new Inspeccion();
        inspeccion.setPuente(puente);
        inspeccion.setTiempo(request.getTiempo());
        inspeccion.setTemperatura(request.getTemperatura());
        inspeccion.setAdministrador(request.getAdministrador());
        inspeccion.setAnioProximaInspeccion(request.getAnioProximaInspeccion());
        inspeccion.setObservacionesGenerales(request.getObservacionesGenerales());

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
        usuario.setNombres(usuarioDTO.getNombres());
        usuario.setApellidos(usuarioDTO.getApellidos());
        usuario.setIdentificacion(usuarioDTO.getIdentificacion());
        usuario.setTipoUsuario(usuarioDTO.getTipoUsuario());
        usuario.setCorreo(usuarioDTO.getCorreo());
        usuario.setMunicipio(usuarioDTO.getMunicipio());
        usuario.setContrasenia(usuarioDTO.getContrasenia());
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
}

package com.bridgecare.inspection.services;
import com.bridgecare.inspection.models.dtos.ComponenteDTO;
import com.bridgecare.inspection.models.dtos.ReparacionDTO;
import com.bridgecare.inspection.models.entities.Inspeccion;
import com.bridgecare.inspection.models.entities.Componente;
import com.bridgecare.inspection.models.entities.Reparacion;
import com.bridgecare.inspection.repositories.ComponenteRepository;
import com.bridgecare.inspection.repositories.InspeccionRepository;
import com.bridgecare.inspection.repositories.ReparacionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.bridgecare.inspection.models.dtos.InspeccionDTO;

import com.bridgecare.common.models.entities.Puente;
import com.bridgecare.common.models.entities.Usuario;

import java.util.List;

@Service
public class InspeccionService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private InspeccionRepository inspeccionRepository;

    @Autowired
    private ComponenteRepository componenteRepository;

    @Autowired
    private ReparacionRepository reparacionRepository;

    @Transactional
    public Long saveInspection(InspeccionDTO request){
        String puenteUrl = "http://puente-service/api/puentes";
        ResponseEntity<Long> response = restTemplate.postForEntity(puenteUrl, request.getPuente(), Long.class);
        Long puenteId = response.getBody();

        Puente puente = new Puente();
        puente.setId(puenteId);
        puente.setNombre(request.getPuente().getNombre());

        Inspeccion inspeccion = new Inspeccion();
        inspeccion.setPuente(puente);
        inspeccion.setTiempo(request.getTiempo());
        inspeccion.setTemperatura(request.getTemperatura());
        inspeccion.setAdministrador(request.getAdministrador());
        inspeccion.setAnioProximaInspeccion(request.getAnioProximaInspeccion());
        inspeccion.setObservacionesGenerales(request.getObservacionesGenerales());

        Usuario usuario = new Usuario();
        usuario.setId(request.getUsuario().getId());
        inspeccion.setUsuario(usuario);

        if (request.getComponente() != null) {
            Componente comp = new Componente();
            comp.setInspeccion(inspeccion);
            componenteRepository.save(comp);
            inspeccion.setComponentes((List<Componente>) comp);
           /*
            ComponenteDTO componente = request.getComponente().get(0);
            if (componente.getReparacion() != null) {
                List<ReparacionDTO> reparaciones = componente.getReparacion();
            }
            */

        }
        // Falta return
    }
}
